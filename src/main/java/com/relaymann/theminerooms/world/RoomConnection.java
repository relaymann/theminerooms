package com.relaymann.theminerooms.world;

import java.util.Objects;

/** Undirected adjacency between two rooms in the topology graph. */
public record RoomConnection(String fromRoomId, String toRoomId, RoomConnectionType type) {
    public RoomConnection {
        if (fromRoomId == null || fromRoomId.isBlank()
                || toRoomId == null || toRoomId.isBlank()) {
            throw new IllegalArgumentException("Room connection endpoints must not be blank");
        }
        fromRoomId = fromRoomId.trim();
        toRoomId = toRoomId.trim();
        if (fromRoomId.equals(toRoomId)) {
            throw new IllegalArgumentException("A room cannot connect to itself");
        }
        type = Objects.requireNonNull(type, "type");
    }

    /** Stable key for duplicate detection, independent of edge direction. */
    public String undirectedKey() {
        return fromRoomId.compareTo(toRoomId) < 0
                ? fromRoomId + "|" + toRoomId
                : toRoomId + "|" + fromRoomId;
    }
}
