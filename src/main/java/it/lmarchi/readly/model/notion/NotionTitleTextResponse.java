package it.lmarchi.readly.model.notion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.lmarchi.readly.model.notion.NotionTitleTextResponse.NotionTextResponse;

/** Represents a text title property response. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(
        name = "text",
        value = NotionTextResponse.class
    )})
public interface NotionTitleTextResponse {
  String type();

  /** Represents a text Notion response. */
  record NotionTextResponse(String type, @JsonProperty("plain_text") String plainText)
      implements NotionTitleTextResponse {
  }
}
