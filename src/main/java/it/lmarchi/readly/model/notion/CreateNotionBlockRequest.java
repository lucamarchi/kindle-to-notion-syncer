package it.lmarchi.readly.model.notion;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Represents a block creation request in a page. */
public record CreateNotionBlockRequest(String object, String type, CreateNotionQuoteRequest quote) {
  public static CreateNotionBlockRequest of(String quote) {
    return new CreateNotionBlockRequest(
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
