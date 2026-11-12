package com.relaymann.theminerooms.world;

import java.util.Objects;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Places one chunk-owned decoration slice during world generation.
 * The caller supplies the registry-backed block-state resolver.
 */
public final class RoomDecorationBlockPlacer {
    private RoomDecorationBlockPlacer() {}

    public static boolean place(WorldGenLevel level, RoomChunkGenerationPlan plan,
                                RoomDecorationSlice decoration, int floorY,
                                Function<String, BlockState> blockResolver) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(plan, "plan");
        Objects.requireNonNull(decoration, "decoration");
        Objects.requireNonNull(blockResolver, "blockResolver");

        RoomChunkSlice room = plan.chunkWork().roomSlice().orElse(null);
        if (room == null || !plan.roomBuildSpec().isPresent()) return false;
        if (room.localChunkX() != decoration.localChunkX()
                || room.localChunkZ() != decoration.localChunkZ()) {
            throw new IllegalArgumentException("Decoration slice belongs to a different room chunk");
        }
        if (floorY < level.getMinBuildHeight()
                || (long) floorY + RoomSliceBlockPlacer.ROOM_HEIGHT - 1 >= level.getMaxBuildHeight()) {
            return false;
        }
        if (!RoomDecorationGeometry.isWorldPositionRepresentable(
                plan.chunkWork().chunkX(), plan.chunkWork().chunkZ())) return false;

        int baseX = plan.chunkWork().chunkX() * ChunkSlicePlanner.CHUNK_SIZE;
        int baseZ = plan.chunkWork().chunkZ() * ChunkSlicePlanner.CHUNK_SIZE;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (RoomDecorationBlock block : decoration.blocks()) {
            if (!RoomDecorationGeometry.belongsToChunk(
                    block, decoration.localChunkX(), decoration.localChunkZ())) {
                throw new IllegalArgumentException("Decoration slice contains a foreign block");
            }
            BlockState state = Objects.requireNonNull(
                    blockResolver.apply(block.blockId()),
                    "No block state resolved for " + block.blockId());
            cursor.set(baseX + RoomDecorationGeometry.blockOffsetInChunkX(block),
                    floorY + block.y(),
                    baseZ + RoomDecorationGeometry.blockOffsetInChunkZ(block));
            level.setBlock(cursor, state, 2);
        }
        return true;
    }
}
