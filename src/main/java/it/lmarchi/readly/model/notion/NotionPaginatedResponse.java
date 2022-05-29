package it.lmarchi.readly.model.notion;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;

/**
 * Represents a paginated Notion response.
 *
 * @param results A list of the endpoint dependent objects.
 * @param hasMore Tells whether the result does not include all the results.
 * @param nextCursor The next page of results; empty if {@link #hasMore} is false.
 */
// XXX: The annotation `@JsonProperty` is required here because Jackson does not fully support Java Record at the
// moment, see here: https://github.com/FasterXML/jackson-databind/issues/2992. Once this issue is resolved, this
// annotation can be dropped.
public record NotionPaginatedResponse(
    List<NotionResultItemResponse> results,
    @JsonProperty("has_more") boolean hasMore,
    @JsonProperty("next_cursor") Optional<String> nextCursor) {}

