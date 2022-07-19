package it.lmarchi.kindletonotionsyncer;

import it.lmarchi.kindletonotionsyncer.model.kindle.KindleHighlight;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/** A component that fetches and parses the highlights from an Amazon Kindle device. */
final class AmazonKindleHighlightParser {
  private static final Logger LOG = LoggerFactory.getLogger(AmazonKindleHighlightParser.class);
  /** A regex matching the structure of the Kindle clippings file. */
  private static final Pattern HIGHLIGHT_PATTERN =
      Pattern.compile(
          "^(.*)\s\\((.*)\\)(\\r?\\n|\\r)-.*(\\r?\\n|\\r){2}(.*)(\\r?\\n|\\r)={10}(\\r?\\n|\\r)+",
          Pattern.MULTILINE);

  private final String kindleHighlightsBasePath;

  AmazonKindleHighlightParser(@Value("${kindle-highlights.base-path}") String kindleHighlightsBasePath) {
    this.kindleHighlightsBasePath = kindleHighlightsBasePath;
  }

  /** Returns the highlights of the books in the provided Amazon Kindle device. */
  List<KindleHighlight> getHighlights() {
    String clippingFileContent = getClippingFileContent();

    return HIGHLIGHT_PATTERN.matcher(clippingFileContent)
        .results()
        .map(AmazonKindleHighlightParser::toHighlight)
        .peek(highlight ->
            LOG.debug("Found highlight for book '{}' of author '{}': {}", highlight.title(), highlight.author(), highlight.content()))
        .filter(highlight -> !highlight.content().isEmpty())
        .toList();
  }

  /** Returns the content of the file containing the Kindle highlights. */
  private String getClippingFileContent() {
    Path clippingFile = Path.of(kindleHighlightsBasePath,  "My clippings.txt");
    try {
      return Files.readString(clippingFile);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read the Kindle clipping file", e);
    }
  }

  /** Returns the highlight of a book from the given regex match. */
  private static KindleHighlight toHighlight(MatchResult matcher) {
    return new KindleHighlight(matcher.group(1), matcher.group(2), matcher.group(5));
  }
}
