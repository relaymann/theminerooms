package com.relaymann.theminerooms.world;

import java.util.Objects;

/** One room in a generated level topology. */
public record RoomNode(String id, GridPosition position, RoomType type) {
    public RoomNode {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Room id must not be blank");
        }
        id = id.trim();
        if (!id.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("Room id must use lowercase letters, digits, '.', '_' or '-'");
        }
        position = Objects.requireNonNull(position, "position");
        type = Objects.requireNonNull(type, "type");
    }
}
