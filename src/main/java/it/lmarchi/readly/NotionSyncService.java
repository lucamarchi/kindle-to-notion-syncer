package it.lmarchi.readly;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import it.lmarchi.readly.model.kindle.KindleHighlight;
import it.lmarchi.readly.model.notion.NotionHighlight;
import java.util.List;
import java.util.Set;

/** A service that syncs highlights from Amazon Kindle to Notion. */
final class NotionSyncService {
  private final AmazonKindleHighlightParser kindleHighlightParser;
  private final NotionProvider notionProvider;

  NotionSyncService(
      AmazonKindleHighlightParser kindleHighlightParser, NotionProvider notionProvider) {
    this.kindleHighlightParser = kindleHighlightParser;
    this.notionProvider = notionProvider;
  }

  /** Retrieves the highlights from the Amazon Kindle device and syncs them to Notion. */
  void syncHighlights() {
    ListMultimap<String, KindleHighlight> kindleHighlightsByTitle =
        Multimaps.index(kindleHighlightParser.getHighlights(), KindleHighlight::title);
    ListMultimap<String, NotionHighlight> notionHighlightsByTitle =
        Multimaps.index(notionProvider.getHighlights(), NotionHighlight::title);

    kindleHighlightsByTitle
        .asMap()
        .entrySet()
        .forEach(
            entry ->
                syncHighlights(
                    List.copyOf(entry.getValue()), notionHighlightsByTitle.get(entry.getKey())));
  }

  /**
   * Syncs the new highlights to Notion; if the book has not been synced yet, creates first a
   * dedicated page.
   *
   * @param highlightsToSync The highlights stored in the Amazon Kindle.
   * @param syncedHighlights The highlights synced to Notion.
   */
  private void syncHighlights(
      List<KindleHighlight> highlightsToSync, List<NotionHighlight> syncedHighlights) {
    KindleHighlight kindleHighlight = getAny(highlightsToSync);
    List<String> quotesToSync = highlightsToSync.stream().map(KindleHighlight::content).toList();

    if (syncedHighlights.isEmpty()) {
      notionProvider.createPage(kindleHighlight.title(), kindleHighlight.author(), quotesToSync);
    } else {
      NotionHighlight notionHighlight = getAny(syncedHighlights);
      List<String> syncedQuotes =
          syncedHighlights.stream().flatMap(h -> h.quotes().stream()).toList();

      notionProvider.addHighlightsToPage(
          notionHighlight.pageId(), getDifferenceBetween(quotesToSync, syncedQuotes));
    }
  }

  /** Returns the first element of the given list; throws an error if empty. */
  private static <T> T getAny(List<T> elements) {
    return elements.stream()
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Expected at least one element"));
  }

  /** Returns the difference between the two lists. */
  private static List<String> getDifferenceBetween(List<String> one, List<String> two) {
    return List.copyOf(Sets.difference(Set.copyOf(one), Set.copyOf(two)));
  }
}
