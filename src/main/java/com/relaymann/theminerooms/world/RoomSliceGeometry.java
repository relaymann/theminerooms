package com.relaymann.theminerooms.world;

/**
 * Pure room-slice geometry calculations.
 *
 * <p>Kept independent of Minecraft classes so geometry can be unit-tested without
 * bootstrapping a Minecraft runtime.</p>
 */
public final class RoomSliceGeometry {
    private static final int ROOM_SIZE = 32;
    private static final int DOORWAY_WIDTH = 3;
    private static final int DOORWAY_HEIGHT = 3;
    private static final int DOORWAY_START = (ROOM_SIZE - DOORWAY_WIDTH) / 2;

    private RoomSliceGeometry() {
    }

    public static boolean isBlockRangeRepresentable(long baseX, long baseZ) {
        return baseX >= Integer.MIN_VALUE && baseZ >= Integer.MIN_VALUE
                && baseX + ChunkSlicePlanner.CHUNK_SIZE - 1L <= Integer.MAX_VALUE
                && baseZ + ChunkSlicePlanner.CHUNK_SIZE - 1L <= Integer.MAX_VALUE;
    }

    public static boolean isRoomBoundaryPosition(RoomChunkSlice slice, int x, int z) {
        return (slice.localChunkZ() == 0 && z == 0)
                || (slice.localChunkX() == 1 && x == 15)
                || (slice.localChunkZ() == 1 && z == 15)
                || (slice.localChunkX() == 0 && x == 0);
    }

    public static boolean isDoorwayPosition(RoomChunkSlice slice, int x, int y, int z) {
        if (y < 1 || y > DOORWAY_HEIGHT) {
            return false;
        }

        int roomLocalX = slice.localChunkX() * ChunkSlicePlanner.CHUNK_SIZE + x;
        int roomLocalZ = slice.localChunkZ() * ChunkSlicePlanner.CHUNK_SIZE + z;
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
