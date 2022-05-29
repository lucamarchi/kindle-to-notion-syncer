package it.lmarchi.readly.model.notion;

import java.util.List;
import it.lmarchi.readly.model.notion.NotionTitleTextResponse.NotionTextResponse;

/** Represents a quote Notion response. */
public record NotionQuoteResponse(List<NotionTextResponse> text) {}
