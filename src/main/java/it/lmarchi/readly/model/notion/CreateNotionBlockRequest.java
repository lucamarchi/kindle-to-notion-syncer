package it.lmarchi.readly.model.notion;

import java.util.List;

/** Represents a block creation request in a page. */
public record CreateNotionBlockRequest(List<CreateNotionChildrenRequest> children) {}
