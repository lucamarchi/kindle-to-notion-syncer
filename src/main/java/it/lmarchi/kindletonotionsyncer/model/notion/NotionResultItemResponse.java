package it.lmarchi.kindletonotionsyncer.model.notion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.lmarchi.kindletonotionsyncer.model.notion.NotionResultItemResponse.NotionBlockResponse;
import it.lmarchi.kindletonotionsyncer.model.notion.NotionResultItemResponse.NotionPageResponse;
import java.util.Map;
import java.util.Optional;

/** Represents a generic Notion object response. */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "object",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(
        name = "block",
        value = NotionBlockResponse.class
    ),
    @JsonSubTypes.Type(
        name = "page",
        value = NotionPageResponse.class
    )})
public interface NotionResultItemResponse {
  String id();

  String object();

  /** Represents a block Notion object. */
  record NotionBlockResponse(String id, String object, Optional<NotionQuoteResponse> quote)
      implements NotionResultItemResponse {
  }

  /** Represents a page Notion object. */
  record NotionPageResponse(String id, String object, Map<String, NotionPagePropertyResponse> properties)
      implements NotionResultItemResponse {
  }
}
