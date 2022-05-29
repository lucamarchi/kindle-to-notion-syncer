package it.lmarchi.readly.model.notion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.lmarchi.readly.model.notion.NotionPagePropertyResponse.NotionTextPropertyResponse;
import it.lmarchi.readly.model.notion.NotionPagePropertyResponse.NotionTitlePropertyResponse;
import java.util.List;

/** Represents the property values of a page in Notion. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(
        name = "rich_text",
        value = NotionTextPropertyResponse.class
    ),
    @JsonSubTypes.Type(
        name = "title",
        value = NotionTitlePropertyResponse.class
    )})
public interface NotionPagePropertyResponse {
  String id();

  String type();

  /** Represents a rich text property of a page. */
  record NotionTextPropertyResponse(
        String id, String type, @JsonProperty("rich_text") List<NotionTitleTextResponse> richText)
      implements NotionPagePropertyResponse {
  }

  /** Represents the title property of a page. */
  record NotionTitlePropertyResponse(String id, String type, List<NotionTitleTextResponse> title)
      implements NotionPagePropertyResponse {
  }
}
