package com.relaymann.theminerooms.world;

/** Pure coordinate mapping for room-local decorations. */
public final class RoomDecorationGeometry {
    private RoomDecorationGeometry() {}

    public static boolean belongsToChunk(RoomDecorationBlock block, int localChunkX, int localChunkZ) {
        return block.localChunkX() == localChunkX && block.localChunkZ() == localChunkZ;
    }

    public static int blockOffsetInChunkX(RoomDecorationBlock block) {
        return block.x() % ChunkSlicePlanner.CHUNK_SIZE;
    }

    public static int blockOffsetInChunkZ(RoomDecorationBlock block) {
        return block.z() % ChunkSlicePlanner.CHUNK_SIZE;
    }

    public static boolean isWorldPositionRepresentable(long chunkX, long chunkZ) {
        long baseX = chunkX * ChunkSlicePlanner.CHUNK_SIZE;
        long baseZ = chunkZ * ChunkSlicePlanner.CHUNK_SIZE;
        return RoomSliceGeometry.isBlockRangeRepresentable(baseX, baseZ);
    }
}
