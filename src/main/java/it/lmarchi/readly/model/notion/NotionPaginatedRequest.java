package it.lmarchi.readly.model.notion;

import java.util.Optional;

/**
 * Represents a request for paginated list Notion results.
 *
 * @param startCursor If provided, fetches the results starting after the cursor.
 */
public record NotionPaginatedRequest(Optional<String> startCursor) {}
