package com.relaymann.theminerooms.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ChunkSlicePlannerTest {
    @Test
    void mapsAllFourChunksToOneRoomWithoutDuplicatingRoomIdentity() {
        ChunkSlicePlanner planner = new ChunkSlicePlanner(twoRoomLayout());
        for (int x = 0; x <= 1; x++) {
            for (int z = 0; z <= 1; z++) {
                RoomChunkSlice slice = planner.plan(x, z).roomSlice().orElseThrow();
                assertEquals("west", slice.roomId());
                assertEquals(x, slice.localChunkX());
                assertEquals(z, slice.localChunkZ());
            }
        }
    }

    @Test
    void emitsDoorwayOnlyOnTheChunkContainingTheSharedRoomBoundary() {
        ChunkSlicePlanner planner = new ChunkSlicePlanner(twoRoomLayout());
        assertTrue(planner.plan(0, 0).roomSlice().orElseThrow().openings().isEmpty());
        assertTrue(planner.plan(1, 0).roomSlice().orElseThrow().openings().stream()
                .anyMatch(o -> o.direction() == RoomDirection.EAST && o.targetRoomId().equals("east")));
        assertTrue(planner.plan(2, 0).roomSlice().orElseThrow().openings().stream()
                .anyMatch(o -> o.direction() == RoomDirection.WEST && o.targetRoomId().equals("west")));
        assertTrue(planner.plan(2, 1).roomSlice().orElseThrow().openings().stream()
                .anyMatch(o -> o.direction() == RoomDirection.WEST));
    }

    @Test
    void chunksOutsideTheGeneratedFootprintHaveNoRoomWork() {
        ChunkSlicePlanner planner = new ChunkSlicePlanner(twoRoomLayout());
        assertFalse(planner.plan(-1, 0).isInsideLevel());
        assertFalse(planner.plan(4, 0).isInsideLevel());
        assertFalse(planner.plan(0, 2).isInsideLevel());
    }

    @Test
    void repeatedChunkRequestsProduceEqualPlans() {
        ChunkSlicePlanner planner = new ChunkSlicePlanner(twoRoomLayout());
        assertEquals(planner.plan(1, 0), planner.plan(1, 0));
    }

    private static LevelLayout twoRoomLayout() {
        return new LevelLayout(10L, 2, 1,
                List.of(
                        new RoomNode("west", new GridPosition(0, 0), RoomType.START),
                        new RoomNode("east", new GridPosition(1, 0), RoomType.STANDARD)),
                List.of(new RoomConnection("west", "east", RoomConnectionType.BACKBONE)));
    }
}
