package com.relaymann.theminerooms.world;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class RoomSliceBlockPlacerTest {
    @Test
    void centersNorthDoorwayAcrossTheTwoChunksOnTheRoomEdge() {
        RoomChunkSlice first = slice(0, 0, RoomDirection.NORTH);
        RoomChunkSlice second = slice(1, 0, RoomDirection.NORTH);

        assertTrue(RoomSliceBlockPlacer.isDoorwayPosition(first, 14, 1, 0));
        assertTrue(RoomSliceBlockPlacer.isDoorwayPosition(first, 15, 3, 0));
        assertTrue(RoomSliceBlockPlacer.isDoorwayPosition(second, 0, 2, 0));
        assertFalse(RoomSliceBlockPlacer.isDoorwayPosition(second, 1, 2, 0));
        assertFalse(RoomSliceBlockPlacer.isDoorwayPosition(first, 14, 4, 0));
        assertFalse(RoomSliceBlockPlacer.isDoorwayPosition(first, 14, 0, 0));
    }

    @Test
    void centersEastDoorwayAcrossTheTwoChunksOnTheRoomEdge() {
        RoomChunkSlice first = slice(0, 0, RoomDirection.EAST);
        RoomChunkSlice second = slice(0, 1, RoomDirection.EAST);

        assertTrue(RoomSliceBlockPlacer.isDoorwayPosition(first, 15, 2, 14));
        assertTrue(RoomSliceBlockPlacer.isDoorwayPosition(first, 15, 2, 15));
        assertTrue(RoomSliceBlockPlacer.isDoorwayPosition(second, 15, 2, 0));
        assertFalse(RoomSliceBlockPlacer.isDoorwayPosition(second, 15, 2, 1));
    }

    @Test
    void southAndWestOpeningsUseTheirMatchingRoomEdges() {
        RoomChunkSlice south = slice(1, 1, RoomDirection.SOUTH);
        RoomChunkSlice west = slice(0, 1, RoomDirection.WEST);

        assertTrue(RoomSliceBlockPlacer.isDoorwayPosition(south, 0, 2, 15));
        assertFalse(RoomSliceBlockPlacer.isDoorwayPosition(south, 1, 2, 15));
        assertTrue(RoomSliceBlockPlacer.isDoorwayPosition(west, 0, 2, 0));
        assertFalse(RoomSliceBlockPlacer.isDoorwayPosition(west, 0, 2, 1));
    }

    private static RoomChunkSlice slice(int chunkX, int chunkZ, RoomDirection direction) {
        return new RoomChunkSlice(
                "test-room",
                RoomType.START,
                chunkX,
                chunkZ,
                List.of(new RoomOpening(direction, "neighbor", RoomConnectionType.BACKBONE)));
    }
}
