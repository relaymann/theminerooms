package com.relaymann.theminerooms.world;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/** Produces bounded chunk-local decoration work without loading or mutating chunks. */
public final class RoomDecorationPlanner {
    public List<RoomDecorationSlice> plan(RoomDecoration decoration) {
        Objects.requireNonNull(decoration, "decoration");
        List<RoomDecorationSlice> slices = new ArrayList<>(4);
        for (int z = 0; z < 2; z++)
            for (int x = 0; x < 2; x++) slices.add(plan(decoration, x, z));
        return List.copyOf(slices);
    }
    public RoomDecorationSlice plan(RoomDecoration decoration, int localChunkX, int localChunkZ) {
        Objects.requireNonNull(decoration, "decoration");
        if (localChunkX < 0 || localChunkX > 1 || localChunkZ < 0 || localChunkZ > 1)
            throw new IllegalArgumentException("Local chunk coordinates must be 0 or 1");
        List<RoomDecorationBlock> owned = decoration.blocks().stream()
                .filter(block -> block.localChunkX() == localChunkX && block.localChunkZ() == localChunkZ)
                .toList();
        return new RoomDecorationSlice(decoration.id(), localChunkX, localChunkZ, owned);
    }
}
