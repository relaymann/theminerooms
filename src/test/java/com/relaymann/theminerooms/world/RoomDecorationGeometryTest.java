package com.relaymann.theminerooms.world;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RoomDecorationGeometryTest {
    @Test void mapsRoomCoordinatesIntoTheOwningChunk() {
        RoomDecorationBlock northWest = block(1, 2, 1);
        RoomDecorationBlock northEast = block(17, 2, 1);
        RoomDecorationBlock southWest = block(1, 2, 17);
        RoomDecorationBlock southEast = block(30, 2, 30);

        assertTrue(RoomDecorationGeometry.belongsToChunk(northWest, 0, 0));
        assertTrue(RoomDecorationGeometry.belongsToChunk(northEast, 1, 0));
        assertTrue(RoomDecorationGeometry.belongsToChunk(southWest, 0, 1));
        assertTrue(RoomDecorationGeometry.belongsToChunk(southEast, 1, 1));
        assertEquals(1, RoomDecorationGeometry.blockOffsetInChunkX(northWest));
        assertEquals(1, RoomDecorationGeometry.blockOffsetInChunkX(northEast));
        assertEquals(14, RoomDecorationGeometry.blockOffsetInChunkZ(southEast));
    }

    @Test void rejectsUnrepresentableChunkWorldCoordinates() {
        assertTrue(RoomDecorationGeometry.isWorldPositionRepresentable(0, 0));
        assertTrue(RoomDecorationGeometry.isWorldPositionRepresentable(-1, -1));
        assertFalse(RoomDecorationGeometry.isWorldPositionRepresentable(
                ((long) Integer.MAX_VALUE / 16) + 1, 0));
    }

    private static RoomDecorationBlock block(int x, int y, int z) {
        return new RoomDecorationBlock(x, y, z, "minecraft:oak_planks");
    }
}
