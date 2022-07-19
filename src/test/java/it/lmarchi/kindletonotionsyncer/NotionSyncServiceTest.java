package it.lmarchi.kindletonotionsyncer;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.lmarchi.kindletonotionsyncer.model.kindle.KindleHighlight;
import it.lmarchi.kindletonotionsyncer.model.notion.NotionHighlight;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
final class NotionSyncServiceTest {
  @Test
  void syncHighlights_WithNewQuotes_ShouldAddTheQuotes() {
    AmazonKindleHighlightParser kindleHighlightParser = mock(AmazonKindleHighlightParser.class);
    when(kindleHighlightParser.getHighlights())
        .thenReturn(
            List.of(
                k("A title", "An author", "Hello!"), k("A title", "An author", "Another hello!")));

    NotionProvider notionProvider = mock(NotionProvider.class);
    when(notionProvider.getHighlights())
        .thenReturn(List.of(n("id", "A title", "An author", "Hello!")));

    NotionSyncService service = new NotionSyncService(kindleHighlightParser, notionProvider);

    assertThatCode(service::syncHighlights).doesNotThrowAnyException();

    verify(notionProvider).addHighlightsToPage("id", List.of("Another hello!"));
    verify(notionProvider, never()).createPage(any(), any(), any());
  }

  @Test
  void syncHighlights_WithNewBooks_ShouldCreateAPageAndAddQuotes() {
    AmazonKindleHighlightParser kindleHighlightParser = mock(AmazonKindleHighlightParser.class);
    when(kindleHighlightParser.getHighlights())
        .thenReturn(
            List.of(
                k("A title", "An author", "Hello!"),
                k("A title", "An author", "Another hello!"),
                k("Another title", "Another author", "Goodbye")));

    NotionProvider notionProvider = mock(NotionProvider.class);
    when(notionProvider.getHighlights())
        .thenReturn(List.of(n("id", "A title", "An author", "Hello!")));

    NotionSyncService service = new NotionSyncService(kindleHighlightParser, notionProvider);

    assertThatCode(service::syncHighlights).doesNotThrowAnyException();

    verify(notionProvider).addHighlightsToPage("id", List.of("Another hello!"));
    verify(notionProvider).createPage("Another title", "Another author", List.of("Goodbye"));
  }

  @Test
  void syncHighlights_WithHighlightNotPresentInKindle_ShouldIgnoreThese() {
    AmazonKindleHighlightParser kindleHighlightParser = mock(AmazonKindleHighlightParser.class);
    when(kindleHighlightParser.getHighlights()).thenReturn(List.of());

    NotionProvider notionProvider = mock(NotionProvider.class);
    when(notionProvider.getHighlights())
        .thenReturn(List.of(n("id", "A title", "An author", "Hello!")));

    NotionSyncService service = new NotionSyncService(kindleHighlightParser, notionProvider);

    assertThatCode(service::syncHighlights).doesNotThrowAnyException();

    verify(notionProvider, never()).addHighlightsToPage(any(), any());
    verify(notionProvider, never()).createPage(any(), any(), any());
  }

  private static KindleHighlight k(String title, String author, String quote) {
    return new KindleHighlight(title, author, quote);
  }

  private static NotionHighlight n(String id, String title, String author, String... quotes) {
    return new NotionHighlight(id, title, author, Arrays.asList(quotes));
  }
}
