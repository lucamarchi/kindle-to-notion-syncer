package it.lmarchi.readly.model.notion;

import java.util.List;

/**
 * Represents a page in Notion with highlights of a book.
 *
 * @param title The title of the book.
 * @param author The author of the book.
 * @param quotes The actual highlights.
 */
public record NotionHighlight (String title, String author, List<String> quotes) {}
