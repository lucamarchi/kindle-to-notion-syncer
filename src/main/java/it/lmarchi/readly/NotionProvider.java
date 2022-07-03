package it.lmarchi.readly;

import com.google.common.collect.Iterables;
import it.lmarchi.readly.model.notion.CreateNotionBlockRequest;
import it.lmarchi.readly.model.notion.CreateNotionChildrenRequest;
import it.lmarchi.readly.model.notion.CreateNotionPageRequest;
import it.lmarchi.readly.model.notion.NotionPagePropertyResponse;
import it.lmarchi.readly.model.notion.NotionPagePropertyResponse.NotionTextPropertyResponse;
import it.lmarchi.readly.model.notion.NotionPagePropertyResponse.NotionTitlePropertyResponse;
import it.lmarchi.readly.model.notion.NotionPaginatedRequest;
import it.lmarchi.readly.model.notion.NotionPaginatedResponse;
import it.lmarchi.readly.model.notion.NotionHighlight;
import it.lmarchi.readly.model.notion.NotionResultItemResponse;
import it.lmarchi.readly.model.notion.NotionResultItemResponse.NotionBlockResponse;
import it.lmarchi.readly.model.notion.NotionResultItemResponse.NotionPageResponse;
import it.lmarchi.readly.model.notion.NotionTitleTextResponse;
import it.lmarchi.readly.model.notion.NotionTitleTextResponse.NotionTextResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.Call;
import retrofit2.Response;

/** A component that interacts with Notion. */
final class NotionProvider {
  private static final Logger LOG = LoggerFactory.getLogger(NotionProvider.class);

  private final NotionClient notionClient;
  private final String bookDatabaseId;
  private final String titleBookColumName;
  private final String authorBookColumName;

  NotionProvider(
      NotionClient notionClient,
      @Value("${notion.book.database-id}") String bookDatabaseId,
      @Value("${notion.book.title:Title}") String titleBookColumName,
      @Value("${notion.book.author-id:Author}") String authorBookColumName) {
    this.notionClient = notionClient;
    this.bookDatabaseId = bookDatabaseId;
    this.titleBookColumName = titleBookColumName;
    this.authorBookColumName = authorBookColumName;
  }

  /** Returns all the highlights stored in Notion. */
  List<NotionHighlight> getHighlights() {
    return getPaginatedResult(
            cursor ->
                notionClient.queryDatabase(bookDatabaseId, new NotionPaginatedRequest(cursor)),
            Optional.empty())
        .filter(NotionPageResponse.class::isInstance)
        .map(NotionPageResponse.class::cast)
        .map(this::getHighlight)
        .peek(
            highlight ->
                LOG.debug(
                    "Found '{}' highlights for book '{}' of author '{}' in Notion",
                    highlight.quotes().size(),
                    highlight.title(),
                    highlight.author()))
        .toList();
  }

  /** Create a page in Notion. */
  void createPage(String title, String author, List<String> quotes) {
    try {
      notionClient
          .createPage(
              CreateNotionPageRequest.of(
                  bookDatabaseId, title, author, titleBookColumName, authorBookColumName, quotes))
          .execute();
    } catch (IOException e) {
      throw new IllegalStateException(
          String.format("Failed to create the page for book '%s' in Notion", title), e);
    }
  }

  /** Adds the given highlights to the Notion page associated with the given ID. */
  void addHighlightsToPage(String pageId, List<String> highlights) {
    List<CreateNotionChildrenRequest> requests =
        highlights.stream().map(CreateNotionChildrenRequest::of).toList();
    try {
      notionClient.addBlocks(pageId, new CreateNotionBlockRequest(requests)).execute();
    } catch (IOException e) {
      throw new IllegalStateException(
          String.format("Failed to add highlights to page '%s' in Notion", pageId), e);
    }
  }

  /** Returns the highlights associated with the given page. */
  private NotionHighlight getHighlight(NotionPageResponse page) {
    Map<String, NotionPagePropertyResponse> properties = page.properties();
    String title =
        getProperty(
            properties,
            titleBookColumName,
            property -> Iterables.getOnlyElement(property.title()),
            NotionTitlePropertyResponse.class);
    String author =
        getProperty(
            properties,
            authorBookColumName,
            property -> Iterables.getOnlyElement(property.richText()),
            NotionTextPropertyResponse.class);

    return new NotionHighlight(page.id(), title, author, getHighlightsFromPage(page));
  }

  /** Returns all the highlights of the given page stored in Notion. */
  private List<String> getHighlightsFromPage(NotionPageResponse page) {
    return getPaginatedResult(cursor -> notionClient.getBlocks(page.id(), null), Optional.empty())
        .filter(NotionBlockResponse.class::isInstance)
        .map(NotionBlockResponse.class::cast)
        .flatMap(block -> block.quote().stream())
        .flatMap(quote -> quote.richText().stream())
        .map(NotionTextResponse::plainText)
        .toList();
  }

  /**
   * Returns all the result of the given request to Notion.
   *
   * <p>This might result in multiple HTTP calls, based on the response.
   */
  private static Stream<NotionResultItemResponse> getPaginatedResult(
      Function<Optional<String>, Call<NotionPaginatedResponse>> notionRequest,
      Optional<String> cursor) {
    NotionPaginatedResponse response = getResponseFromCursor(notionRequest, cursor);
    if (response.nextCursor().isEmpty()) {
      return response.results().stream();
    }

    return Stream.concat(
        response.results().stream(), getPaginatedResult(notionRequest, response.nextCursor()));
  }

  /** Executes the given request to Notion starting from the given cursor. */
  private static NotionPaginatedResponse getResponseFromCursor(
      Function<Optional<String>, Call<NotionPaginatedResponse>> notionRequest,
      Optional<String> cursor) {
    try {
      Response<NotionPaginatedResponse> execute = notionRequest.apply(cursor).execute();
      if (!execute.isSuccessful()) {
        throw new IllegalStateException(
            String.format("Unexpected error from Notion: %s", execute.errorBody().string()));
      }
      return execute.body();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to send a request to Notion", e);
    }
  }

  /** Returns the value of the page property associated with the given column name and type. */
  private static <T extends NotionPagePropertyResponse> String getProperty(
      Map<String, NotionPagePropertyResponse> pageProperties,
      String columnName,
      Function<T, NotionTitleTextResponse> getText,
      Class<T> columnType) {
    return Optional.ofNullable(pageProperties.get(columnName))
        .filter(columnType::isInstance)
        .map(columnType::cast)
        .map(getText)
        .filter(NotionTextResponse.class::isInstance)
        .map(NotionTextResponse.class::cast)
        .map(NotionTextResponse::plainText)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format("Database must have a column with name '%s'", columnName)));
  }
}
