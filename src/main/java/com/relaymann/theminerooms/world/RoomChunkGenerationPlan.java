package com.relaymann.theminerooms.world;

import java.util.Objects;
import java.util.Optional;

/** Chunk-local topology and authored room metadata; contains no world mutations. */
public record RoomChunkGenerationPlan(ChunkWorkPlan chunkWork,
                                      Optional<RoomBuildSpec> roomBuildSpec) {
    public RoomChunkGenerationPlan {
        chunkWork = Objects.requireNonNull(chunkWork, "chunkWork");
        roomBuildSpec = Objects.requireNonNull(roomBuildSpec, "roomBuildSpec");
        if (chunkWork.roomSlice().isPresent() != roomBuildSpec.isPresent()) {
            throw new IllegalArgumentException("Chunk slice and room build specification must be present together");
        }
        if (chunkWork.roomSlice().isPresent()
                && !chunkWork.roomSlice().orElseThrow().roomId()
                .equals(roomBuildSpec.orElseThrow().roomId())) {
            throw new IllegalArgumentException("Chunk slice and room build specification refer to different rooms");
        }
    }
}
