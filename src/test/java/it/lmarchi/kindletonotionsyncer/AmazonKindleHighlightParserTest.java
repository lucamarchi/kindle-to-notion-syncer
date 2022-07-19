package it.lmarchi.kindletonotionsyncer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import it.lmarchi.kindletonotionsyncer.model.kindle.KindleHighlight;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
final class AmazonKindleHighlightParserTest {
  private static final KindleHighlight HIGHLIGHT_1 =
      new KindleHighlight("Test title with (parentheses)", "Author", "Hello people!");
  private static final KindleHighlight HIGHLIGHT_2 =
      new KindleHighlight("Another book", "Author1;Author2", "Another content.");

  @Test
  void getHighlights() {
    AmazonKindleHighlightParser parser =
        new AmazonKindleHighlightParser("src/test/resources/documents");

    assertThat(parser.getHighlights()).containsExactly(HIGHLIGHT_1, HIGHLIGHT_2);
  }

  @Test
  void getHighlightsWhenKindleNotFound() {
    AmazonKindleHighlightParser parser = new AmazonKindleHighlightParser("not-existing-path");

    assertThatThrownBy(parser::getHighlights)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Unable to read the Kindle clipping file");
  }
}
