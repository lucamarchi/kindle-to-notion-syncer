package it.lmarchi.kindletonotionsyncer.model.notion;

import java.util.List;

/**
 * Represents a page in Notion with highlights of a book.
 *
 * @param pageId The ID of the Notion page.
 * @param title The title of the book.
 * @param author The author of the book.
 * @param quotes The actual highlights.
 */
public record NotionHighlight (String pageId, String title, String author, List<String> quotes) {}
