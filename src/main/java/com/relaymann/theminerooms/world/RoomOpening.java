package com.relaymann.theminerooms.world;

import java.util.Objects;

/** A topology edge rendered as an opening between two neighboring rooms. */
public record RoomOpening(RoomDirection direction, String targetRoomId, RoomConnectionType connectionType) {
    public RoomOpening {
        direction = Objects.requireNonNull(direction, "direction");
        if (targetRoomId == null || targetRoomId.isBlank()) {
            throw new IllegalArgumentException("targetRoomId must not be blank");
        }
        targetRoomId = targetRoomId.trim();
        connectionType = Objects.requireNonNull(connectionType, "connectionType");
    }
}
