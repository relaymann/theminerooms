package com.relaymann.theminerooms.world;

import java.util.List;
import java.util.Objects;

/** The part of one 32x32-block room that belongs to a single 16x16 chunk. */
public record RoomChunkSlice(String roomId, RoomType roomType, int localChunkX,
                             int localChunkZ, List<RoomOpening> openings) {
    public RoomChunkSlice {
        if (roomId == null || roomId.isBlank()) throw new IllegalArgumentException("roomId must not be blank");
        roomId = roomId.trim();
        roomType = Objects.requireNonNull(roomType, "roomType");
        if (localChunkX < 0 || localChunkX > 1 || localChunkZ < 0 || localChunkZ > 1) {
            throw new IllegalArgumentException("Local chunk coordinates must be 0 or 1");
        }
        openings = List.copyOf(Objects.requireNonNull(openings, "openings"));
    }
}
