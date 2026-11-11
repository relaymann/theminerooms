package com.relaymann.theminerooms.world;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Writes the geometry for exactly one planned room chunk slice.
 *
 * <p>This class deliberately does not request or load neighboring chunks. Every write is
 * constrained to the 16x16 footprint represented by the supplied {@link ChunkWorkPlan}.</p>
 *
 * <p>The level bootstrap / placed-feature integration is a separate responsibility; callers
 * should invoke this from the target level's chunk-generation pipeline, not from a chunk-load
 * event.</p>
 */
public final class RoomSliceBlockPlacer {
    public static final int ROOM_HEIGHT = 8;
    public static final int DOORWAY_WIDTH = 3;
    public static final int DOORWAY_HEIGHT = 3;
    private static final int DOORWAY_START = (32 - DOORWAY_WIDTH) / 2;

    private RoomSliceBlockPlacer() {
    }

    /**
     * Places floor, ceiling, interior fill, perimeter walls, and any openings owned by this chunk.
     *
     * @param level world-generation view to write into
     * @param plan plan for the chunk currently being generated
     * @param floorY bottom Y coordinate of the room floor
     * @param floorState floor block state
     * @param wallState perimeter wall block state
     * @param ceilingState ceiling block state
     * @param interiorState block state used to clear/fill the room interior (normally air)
     * @return true if a room slice was placed; false if the chunk is outside the level footprint
     *         or the requested vertical room bounds do not fit in the dimension
     */
    public static boolean place(
            WorldGenLevel level,
            ChunkWorkPlan plan,
            int floorY,
            BlockState floorState,
            BlockState wallState,
            BlockState ceilingState,
            BlockState interiorState) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(plan, "plan");
        Objects.requireNonNull(floorState, "floorState");
        Objects.requireNonNull(wallState, "wallState");
        Objects.requireNonNull(ceilingState, "ceilingState");
        Objects.requireNonNull(interiorState, "interiorState");

        RoomChunkSlice slice = plan.roomSlice().orElse(null);
        if (slice == null) {
            return false;
        }

        long ceilingYLong = (long) floorY + ROOM_HEIGHT - 1;
        if (floorY < level.getMinBuildHeight() || ceilingYLong >= level.getMaxBuildHeight()) {
            return false;
        }
        int ceilingY = (int) ceilingYLong;

        int baseX = plan.chunkX() * 16;
        int baseZ = plan.chunkZ() * 16;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                cursor.set(baseX + localX, floorY, baseZ + localZ);
                level.setBlock(cursor, floorState, 2);

                cursor.set(baseX + localX, ceilingY, baseZ + localZ);
                level.setBlock(cursor, ceilingState, 2);

                for (int localY = 1; localY < ROOM_HEIGHT - 1; localY++) {
                    cursor.set(baseX + localX, floorY + localY, baseZ + localZ);
                    level.setBlock(cursor, interiorState, 2);
                }

                if (!isRoomBoundaryPosition(slice, localX, localZ)) {
                    continue;
                }

                for (int localY = 1; localY < ROOM_HEIGHT - 1; localY++) {
                    if (isDoorwayPosition(slice, localX, localY, localZ)) {
                        continue;
                    }
                    cursor.set(baseX + localX, floorY + localY, baseZ + localZ);
                    level.setBlock(cursor, wallState, 2);
                }
            }
        }
        return true;
    }

    static boolean isRoomBoundaryPosition(RoomChunkSlice slice, int x, int z) {
        return (slice.localChunkZ() == 0 && z == 0)
                || (slice.localChunkX() == 1 && x == 15)
                || (slice.localChunkZ() == 1 && z == 15)
                || (slice.localChunkX() == 0 && x == 0);
    }

    static boolean isDoorwayPosition(RoomChunkSlice slice, int x, int y, int z) {
        if (y < 1 || y > DOORWAY_HEIGHT) {
            return false;
        }

        int roomLocalX = slice.localChunkX() * 16 + x;
        int roomLocalZ = slice.localChunkZ() * 16 + z;
        for (RoomOpening opening : slice.openings()) {
            boolean insideDoorway = switch (opening.direction()) {
                case NORTH -> z == 0 && isCentered(roomLocalX);
                case EAST -> x == 15 && isCentered(roomLocalZ);
                case SOUTH -> z == 15 && isCentered(roomLocalX);
                case WEST -> x == 0 && isCentered(roomLocalZ);
            };
            if (insideDoorway) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCentered(int coordinate) {
        return coordinate >= DOORWAY_START && coordinate < DOORWAY_START + DOORWAY_WIDTH;
    }
}
