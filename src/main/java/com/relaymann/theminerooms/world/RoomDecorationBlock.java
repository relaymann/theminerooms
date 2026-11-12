package com.relaymann.theminerooms.world;
import java.util.Objects;
/** One decorative block in room-local coordinates. */
public record RoomDecorationBlock(int x, int y, int z, String blockId) {
    private static final String BLOCK_ID = "[a-z0-9_.-]+:[a-z0-9_./-]+";
    public RoomDecorationBlock {
        if (x < 1 || x > 30 || z < 1 || z > 30)
            throw new IllegalArgumentException("Decoration X/Z must stay inside the room perimeter (1..30)");
        if (y < 1 || y >= RoomSliceBlockPlacer.ROOM_HEIGHT - 1)
            throw new IllegalArgumentException("Decoration Y must stay between floor and ceiling");
        Objects.requireNonNull(blockId, "blockId");
        blockId = blockId.trim();
        if (!blockId.matches(BLOCK_ID))
            throw new IllegalArgumentException("blockId must be a namespaced block identifier");
    }
    public int localChunkX() { return x / ChunkSlicePlanner.CHUNK_SIZE; }
    public int localChunkZ() { return z / ChunkSlicePlanner.CHUNK_SIZE; }
}
