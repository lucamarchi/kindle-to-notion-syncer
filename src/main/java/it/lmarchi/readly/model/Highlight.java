package it.lmarchi.readly.model;

import java.util.Objects;

/** Represents a highlight of a book. */
public record Highlight(String title, String author, String content) {
  public Highlight {
    Objects.requireNonNull(title);
    Objects.requireNonNull(author);
    Objects.requireNonNull(content);
  }
}
