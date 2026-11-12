package com.relaymann.theminerooms.world;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable, prevalidated plan for assigning every room a template and palette.
 * Chunk queries are delegated to the indexed planner and do not load or mutate chunks.
 */
public final class RoomGenerationPlan {
    private final ChunkSlicePlanner chunkPlanner;
    private final Map<String, RoomBuildSpec> roomsById;

    RoomGenerationPlan(ChunkSlicePlanner chunkPlanner, Map<String, RoomBuildSpec> roomsById) {
        this.chunkPlanner = Objects.requireNonNull(chunkPlanner, "chunkPlanner");
        this.roomsById = Map.copyOf(Objects.requireNonNull(roomsById, "roomsById"));
    }

    public Map<String, RoomBuildSpec> rooms() {
        return roomsById;
    }

    public Optional<RoomBuildSpec> room(String roomId) {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("roomId must not be blank");
        }
        return Optional.ofNullable(roomsById.get(roomId.trim()));
    }

    public RoomChunkGenerationPlan planChunk(int chunkX, int chunkZ) {
        ChunkWorkPlan work = chunkPlanner.plan(chunkX, chunkZ);
        if (work.roomSlice().isEmpty()) {
            return new RoomChunkGenerationPlan(work, Optional.empty());
        }
        String roomId = work.roomSlice().orElseThrow().roomId();
        RoomBuildSpec spec = roomsById.get(roomId);
        if (spec == null) {
            throw new IllegalStateException("Missing prevalidated build specification for " + roomId);
        }
        return new RoomChunkGenerationPlan(work, Optional.of(spec));
    }
}
