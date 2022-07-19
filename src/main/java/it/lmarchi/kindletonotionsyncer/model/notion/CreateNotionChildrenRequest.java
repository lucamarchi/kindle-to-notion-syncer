package it.lmarchi.kindletonotionsyncer.model.notion;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Represents a children block creation request in a page. */
public record CreateNotionChildrenRequest(String object, String type, CreateNotionQuoteRequest quote) {
  public static CreateNotionChildrenRequest of(String quote) {
    return new CreateNotionChildrenRequest(
        "block",
        "quote",
        new CreateNotionQuoteRequest(
            List.of(
                new CreateTextNotionQuoteRequest("text", new TextContentNotionPageRequest(quote))), "default"));
  }
}

/** Represents a quote creation request. */
record CreateNotionQuoteRequest(@JsonProperty("rich_text") List<CreateTextNotionQuoteRequest> richText, String color) {}

/** Represents the textual content of a quote. */
record CreateTextNotionQuoteRequest(String type, TextContentNotionPageRequest text) {}
