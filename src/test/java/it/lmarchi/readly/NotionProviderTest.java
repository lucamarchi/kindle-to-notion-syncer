package it.lmarchi.readly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.lmarchi.readly.model.notion.CreateNotionBlockRequest;
import it.lmarchi.readly.model.notion.CreateNotionChildrenRequest;
import it.lmarchi.readly.model.notion.CreateNotionPageRequest;
import it.lmarchi.readly.model.notion.NotionHighlight;
import it.lmarchi.readly.model.notion.NotionPagePropertyResponse;
import it.lmarchi.readly.model.notion.NotionPagePropertyResponse.NotionTextPropertyResponse;
import it.lmarchi.readly.model.notion.NotionPaginatedRequest;
import it.lmarchi.readly.model.notion.NotionPaginatedResponse;
import it.lmarchi.readly.model.notion.NotionQuoteResponse;
import it.lmarchi.readly.model.notion.NotionResultItemResponse.NotionBlockResponse;
import it.lmarchi.readly.model.notion.NotionResultItemResponse.NotionPageResponse;
import it.lmarchi.readly.model.notion.NotionTitleTextResponse.NotionTextResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;

@Tag("unit")
@SuppressWarnings("unchecked")
final class NotionProviderTest {
  private static final String AUTHOR_KEY = "Author";
  private static final String DATABASE_ID = "id";
  private static final String DEFAULT_BLOCK_ID = "blockId";
  private static final String DEFAULT_PAGE_ID = "pageId";
  private static final String TITLE_KEY = "Title";

  @Test
  void getHighlights_ForASinglePage_ShouldReturnThePage() throws IOException {
    Response<NotionPaginatedResponse> pageResponse = mock(Response.class);
    when(pageResponse.isSuccessful()).thenReturn(true);
    when(pageResponse.body())
        .thenReturn(
            new NotionPaginatedResponse(
                List.of(pageResponse("Nice book", "Serious Author")), false, Optional.empty()));
    Call<NotionPaginatedResponse> callPageResponse = mock(Call.class);
    when(callPageResponse.execute()).thenReturn(pageResponse);

    Response<NotionPaginatedResponse> blockResponse = mock(Response.class);
    when(blockResponse.isSuccessful()).thenReturn(true);
    when(blockResponse.body())
        .thenReturn(
            new NotionPaginatedResponse(
                List.of(blockResponse("quote1", "quote2")), false, Optional.empty()));
    Call<NotionPaginatedResponse> callBlockResponse = mock(Call.class);
    when(callBlockResponse.execute()).thenReturn(blockResponse);

    NotionClient client = mock(NotionClient.class);
    when(client.queryDatabase(DATABASE_ID, new NotionPaginatedRequest(Optional.empty())))
        .thenReturn(callPageResponse);
    when(client.getBlocks(DEFAULT_PAGE_ID, null)).thenReturn(callBlockResponse);

    assertThat(provider(client).getHighlights())
        .containsExactly(
            new NotionHighlight(DEFAULT_PAGE_ID, "Nice book", "Serious Author", List.of("quote1", "quote2")));
  }

  @Test
  void getHighlights_WithPagination_ShouldReturnThePages() throws IOException {
    Response<NotionPaginatedResponse> pageResponse1 = mock(Response.class);
    when(pageResponse1.isSuccessful()).thenReturn(true);
    when(pageResponse1.body())
        .thenReturn(
            new NotionPaginatedResponse(
                List.of(pageResponse("Nice book", "Serious Author")), true, Optional.of("1")));
    Call<NotionPaginatedResponse> callPageResponse1 = mock(Call.class);
    when(callPageResponse1.execute()).thenReturn(pageResponse1);

    Response<NotionPaginatedResponse> pageResponse2 = mock(Response.class);
    when(pageResponse2.isSuccessful()).thenReturn(true);
    when(pageResponse2.body())
        .thenReturn(
            new NotionPaginatedResponse(
                List.of(pageResponse("Another book", "Another Serious Author")),
                false,
                Optional.empty()));
    Call<NotionPaginatedResponse> callPageResponse2 = mock(Call.class);
    when(callPageResponse2.execute()).thenReturn(pageResponse2);

    Response<NotionPaginatedResponse> blockResponse = mock(Response.class);
    when(blockResponse.isSuccessful()).thenReturn(true);
    when(blockResponse.body())
        .thenReturn(new NotionPaginatedResponse(List.of(), false, Optional.empty()));
    Call<NotionPaginatedResponse> callBlockResponse = mock(Call.class);
    when(callBlockResponse.execute()).thenReturn(blockResponse);

    NotionClient client = mock(NotionClient.class);
    when(client.queryDatabase(DATABASE_ID, new NotionPaginatedRequest(Optional.empty())))
        .thenReturn(callPageResponse1);
    when(client.queryDatabase(DATABASE_ID, new NotionPaginatedRequest(Optional.of("1"))))
        .thenReturn(callPageResponse2);
    when(client.getBlocks(DEFAULT_PAGE_ID, null)).thenReturn(callBlockResponse);

    assertThat(provider(client).getHighlights())
        .containsExactly(
            new NotionHighlight(DEFAULT_PAGE_ID, "Nice book", "Serious Author", List.of()),
            new NotionHighlight(DEFAULT_PAGE_ID, "Another book", "Another Serious Author", List.of()));
  }

  @Test
  void getHighlights_WithMissingTitle_ShouldThrowAnError() throws IOException {
    Response<NotionPaginatedResponse> pageResponse = mock(Response.class);
    when(pageResponse.isSuccessful()).thenReturn(true);
    when(pageResponse.body())
        .thenReturn(
            new NotionPaginatedResponse(
                List.of(new NotionPageResponse(DEFAULT_PAGE_ID, "page", Map.of())),
                false,
                Optional.empty()));
    Call<NotionPaginatedResponse> callPageResponse = mock(Call.class);
    when(callPageResponse.execute()).thenReturn(pageResponse);

    NotionClient client = mock(NotionClient.class);
    when(client.queryDatabase(DATABASE_ID, new NotionPaginatedRequest(Optional.empty())))
        .thenReturn(callPageResponse);

    assertThatCode(() -> provider(client).getHighlights())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(String.format("Database must have a column with name '%s'", TITLE_KEY));
  }

  @Test
  void getHighlights_ForEmptyDatabase_ShouldReturnEmptyList() throws IOException {
    Response<NotionPaginatedResponse> pageResponse = mock(Response.class);
    when(pageResponse.isSuccessful()).thenReturn(true);
    when(pageResponse.body())
        .thenReturn(new NotionPaginatedResponse(List.of(), false, Optional.empty()));
    Call<NotionPaginatedResponse> callPageResponse = mock(Call.class);
    when(callPageResponse.execute()).thenReturn(pageResponse);

    NotionClient client = mock(NotionClient.class);
    when(client.queryDatabase(DATABASE_ID, new NotionPaginatedRequest(Optional.empty())))
        .thenReturn(callPageResponse);

    assertThat(provider(client).getHighlights()).isEmpty();
  }

  @Test
  void getHighlights_WithUnsuccessfulCall_ShouldPropagateTheError() throws IOException {
    ResponseBody body = mock(ResponseBody.class);
    when(body.string()).thenReturn("error");
    Response<NotionPaginatedResponse> pageResponse = mock(Response.class);
    when(pageResponse.isSuccessful()).thenReturn(false);
    when(pageResponse.errorBody()).thenReturn(body);
    Call<NotionPaginatedResponse> callPageResponse = mock(Call.class);
    when(callPageResponse.execute()).thenReturn(pageResponse);

    NotionClient client = mock(NotionClient.class);
    when(client.queryDatabase(DATABASE_ID, new NotionPaginatedRequest(Optional.empty())))
        .thenReturn(callPageResponse);

    assertThatCode(() -> provider(client).getHighlights())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Unexpected error from Notion: error");
  }

  @Test
  void createPage_ForValidDatabase_ShouldCreateThePage() throws IOException {
    Response<Void> pageResponse = mock(Response.class);
    Call<Void> callPageResponse = mock(Call.class);
    when(callPageResponse.execute()).thenReturn(pageResponse);

    NotionClient client = mock(NotionClient.class);
    when(client.createPage(toPageRequest("New book", "New author", List.of("quote1", "quote2"))))
        .thenReturn(callPageResponse);

    assertThatCode(
            () ->
                provider(client).createPage("New book", "New author", List.of("quote1", "quote2")))
        .doesNotThrowAnyException();
  }

  @Test
  void addHighlightsToPage_ForValidDatabase_ShouldAddTheHighlights() throws IOException {
    Response<Void> blockResponse = mock(Response.class);
    Call<Void> callBlockResponse = mock(Call.class);
    when(callBlockResponse.execute()).thenReturn(blockResponse);

    NotionClient client = mock(NotionClient.class);
    when(client.addBlocks(
            DEFAULT_PAGE_ID, blockRequest(child("quote1"), child("quote2"))))
        .thenReturn(callBlockResponse);

    assertThatCode(
            () ->
                provider(client).addHighlightsToPage(DEFAULT_PAGE_ID, List.of("quote1", "quote2")))
        .doesNotThrowAnyException();
  }

  private static NotionProvider provider(NotionClient client) {
    return new NotionProvider(client, DATABASE_ID, TITLE_KEY, AUTHOR_KEY);
  }

  private static NotionPageResponse pageResponse(String title, String author) {
    return new NotionPageResponse(
        DEFAULT_PAGE_ID,
        "page",
        Map.of(
            TITLE_KEY,
                new NotionPagePropertyResponse.NotionTitlePropertyResponse(
                    "title", "title", List.of(new NotionTextResponse("text", title))),
            AUTHOR_KEY,
                new NotionTextPropertyResponse(
                    "id1", "text", List.of(new NotionTextResponse("text", author)))));
  }

  private static NotionBlockResponse blockResponse(String... quotes) {
    List<NotionTextResponse> quoteResponse =
        Arrays.stream(quotes).map(quote -> new NotionTextResponse("quote", quote)).toList();
    return new NotionBlockResponse(
        DEFAULT_BLOCK_ID, "block", Optional.of(new NotionQuoteResponse(quoteResponse)));
  }

  private static CreateNotionPageRequest toPageRequest(
      String title, String author, List<String> quotes) {
    return CreateNotionPageRequest.of(DATABASE_ID, title, author, TITLE_KEY, AUTHOR_KEY, quotes);
  }

  private static CreateNotionChildrenRequest child(String quote) {
    return CreateNotionChildrenRequest.of(quote);
  }

  private static CreateNotionBlockRequest blockRequest(CreateNotionChildrenRequest... requests) {
    return new CreateNotionBlockRequest(List.of(requests));
  }
}
