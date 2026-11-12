package com.relaymann.theminerooms.world;
import java.util.List;
import java.util.Objects;
/** Immutable subset of a room decoration owned by one chunk. */
public record RoomDecorationSlice(String decorationId, int localChunkX, int localChunkZ,
                                  List<RoomDecorationBlock> blocks) {
    public RoomDecorationSlice {
        if (decorationId == null || decorationId.isBlank())
            throw new IllegalArgumentException("decorationId must not be blank");
        decorationId = decorationId.trim();
        if (localChunkX < 0 || localChunkX > 1 || localChunkZ < 0 || localChunkZ > 1)
            throw new IllegalArgumentException("Local chunk coordinates must be 0 or 1");
        blocks = List.copyOf(Objects.requireNonNull(blocks, "blocks"));
        for (RoomDecorationBlock block : blocks) {
            Objects.requireNonNull(block, "block");
            if (block.localChunkX() != localChunkX || block.localChunkZ() != localChunkZ)
                throw new IllegalArgumentException("Decoration slice contains a block owned by another chunk");
        }
    }
}
