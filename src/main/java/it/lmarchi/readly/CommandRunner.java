package it.lmarchi.readly;

import it.lmarchi.readly.CommandRunner.HighlightsSyncCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Import;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IFactory;

/** A component defining the CLI commands of the application. */
@Import(HighlightsSyncCommand.class)
final class CommandRunner implements CommandLineRunner {
  private final HighlightsSyncCommand command;
  private final IFactory factory;

  CommandRunner(HighlightsSyncCommand command, IFactory factory) {
    this.command = command;
    this.factory = factory;
  }

  @Override
  public void run(String... args) {
    new CommandLine(command, factory).execute(args);
  }

  /** The CLI command that syncs the highlights from Kindle to Notion. */
  @Command(name = "sync", mixinStandardHelpOptions = true)
  static final class HighlightsSyncCommand implements Runnable {
    private final NotionSyncService notionSyncService;

    HighlightsSyncCommand(NotionSyncService notionSyncService) {
      this.notionSyncService = notionSyncService;
    }

    @Override
    public void run() {
      notionSyncService.syncHighlights();
    }
  }
}
