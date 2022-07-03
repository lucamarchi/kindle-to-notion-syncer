package it.lmarchi.readly.model.notion;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import it.lmarchi.readly.model.notion.NotionTitleTextResponse.NotionTextResponse;

/** Represents a quote Notion response. */
// XXX: Checks how to deserialize `null` values to an empty Collection for Java Records.
//public record NotionQuoteResponse(Optional<List<NotionTextResponse>> text) {}
public record NotionQuoteResponse(@JsonProperty("rich_text") List<NotionTextResponse> richText) {}
