package it.lmarchi.kindletonotionsyncer.model.kindle;

import java.util.Objects;

/**
 * Represents a highlight of a book in an Amazon Kindle.
 *
 * @param title The title of the book.
 * @param author The author of the book.
 * @param content The content of the highlight.
 */
public record KindleHighlight(String title, String author, String content) {
  public KindleHighlight {
    Objects.requireNonNull(title);
    Objects.requireNonNull(author);
    Objects.requireNonNull(content);
  }
}
