package com.relaymann.theminerooms.world;

import java.util.Objects;
import java.util.Optional;

/** Immutable work description for one requested chunk; contains no block writes. */
public record ChunkWorkPlan(int chunkX, int chunkZ, Optional<RoomChunkSlice> roomSlice) {
    public ChunkWorkPlan {
        roomSlice = Objects.requireNonNull(roomSlice, "roomSlice");
    }
    public boolean isInsideLevel() { return roomSlice.isPresent(); }
}
