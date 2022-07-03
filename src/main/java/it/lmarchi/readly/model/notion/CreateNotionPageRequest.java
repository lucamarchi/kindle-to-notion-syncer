package it.lmarchi.readly.model.notion;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Represents a request for creating a page in Notion.
 *
 * @param parent The parent of the page.
 * @param properties The properties of the page to create.
 */
public record CreateNotionPageRequest(ParentNotionPageRequest parent, Map<String, TextualNotionPageRequest> properties, List<CreateNotionChildrenRequest> children) {
  public static CreateNotionPageRequest of(String databaseId, String title, String author, String titleColumn, String authorColumn, List<String> quotes) {
    return new CreateNotionPageRequest(
        new ParentNotionPageRequest(databaseId),
        Map.of(
            titleColumn,
            new TitleNotionPageRequest(List.of(new TextNotionPageRequest(new TextContentNotionPageRequest(title)))),
            authorColumn,
            new RichTextNotionPageRequest(List.of(new TextNotionPageRequest(new TextContentNotionPageRequest(author))))
        ),
        quotes.stream().map(CreateNotionChildrenRequest::of).toList());
  }
}

/** Represents the parent of the page to create. */
record ParentNotionPageRequest(@JsonProperty("database_id") String databaseId) {}

/** Represents a generic textual element in the page. */
interface TextualNotionPageRequest {}

/** Represents a title element in the page. */
record TitleNotionPageRequest(List<TextNotionPageRequest> title) implements TextualNotionPageRequest {}

/** Represents a rich text element in the page. */
record RichTextNotionPageRequest(@JsonProperty("rich_text") List<TextNotionPageRequest> richText) implements TextualNotionPageRequest {}

/** Represents a text in the page. */
record TextNotionPageRequest(TextContentNotionPageRequest text) {}

/** Represents the textual content in the page. */
record TextContentNotionPageRequest(String content) {}
