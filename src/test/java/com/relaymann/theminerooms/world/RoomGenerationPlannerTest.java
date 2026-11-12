package com.relaymann.theminerooms.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RoomGenerationPlannerTest {
    private final RoomGenerationPlanner planner = new RoomGenerationPlanner();

    @Test
    void createsConsistentAssignmentsAndChunkPlans() {
        LevelLayout layout = threeRoomLine();
        RoomPalette palette = palette("default");
        RoomTemplate template = new RoomTemplate("generic", "default",
                Set.of(RoomType.START, RoomType.CORRIDOR, RoomType.DEAD_END),
                Set.of(RoomDirection.NORTH, RoomDirection.EAST, RoomDirection.SOUTH, RoomDirection.WEST));

        RoomGenerationPlan plan = planner.plan(layout, List.of(template), List.of(palette), -10, -20);

        assertEquals(3, plan.rooms().size());
        var chunk = plan.planChunk(-10, -20);
        assertTrue(chunk.chunkWork().isInsideLevel());
        assertEquals("start", chunk.roomBuildSpec().orElseThrow().roomId());
        assertEquals("generic", chunk.roomBuildSpec().orElseThrow().template().id());
        assertEquals("default", chunk.roomBuildSpec().orElseThrow().palette().id());
        assertEquals(Set.of(RoomDirection.EAST),
                chunk.roomBuildSpec().orElseThrow().requiredOpenings());
    }

    @Test
    void chunkOutsideLayoutHasNoBuildSpecification() {
        RoomTemplate template = universalTemplate();
        RoomGenerationPlan plan = planner.plan(
                threeRoomLine(), List.of(template), List.of(palette("default")), -10, -20);

        var outside = plan.planChunk(100, 100);
        assertFalse(outside.chunkWork().isInsideLevel());
        assertTrue(outside.roomBuildSpec().isEmpty());
    }

    @Test
    void refusesMissingAndDuplicatePalettesBeforeReturningAPlan() {
        RoomTemplate template = universalTemplate();
        assertThrows(IllegalArgumentException.class, () -> planner.plan(
                threeRoomLine(), List.of(template), List.of()));
        assertThrows(IllegalArgumentException.class, () -> planner.plan(
                threeRoomLine(), List.of(template),
                List.of(palette("default"), palette("default"))));
    }

    @Test
    void refusesTemplateThatCannotSupportAllRoomsAndOpenings() {
        RoomTemplate corridorOnly = new RoomTemplate("corridor", Set.of(RoomType.CORRIDOR),
                Set.of(RoomDirection.WEST, RoomDirection.EAST));
        assertThrows(IllegalArgumentException.class, () -> planner.plan(
                threeRoomLine(), List.of(corridorOnly), List.of(palette("default"))));
    }

    @Test
    void paletteRejectsMalformedBlockIdentifiers() {
        assertThrows(IllegalArgumentException.class, () -> new RoomPalette(
                "bad", "stone", "minecraft:stone", "minecraft:stone", "minecraft:air"));
    }

    private static RoomTemplate universalTemplate() {
        return new RoomTemplate("generic", "default",
                Set.of(RoomType.START, RoomType.CORRIDOR, RoomType.DEAD_END),
                Set.of(RoomDirection.NORTH, RoomDirection.EAST, RoomDirection.SOUTH, RoomDirection.WEST));
    }

    private static RoomPalette palette(String id) {
        return new RoomPalette(id, "minecraft:stone", "minecraft:yellow_concrete",
                "minecraft:stone", "minecraft:air");
    }

    private static LevelLayout threeRoomLine() {
        List<RoomNode> rooms = List.of(
                new RoomNode("start", new GridPosition(0, 0), RoomType.START),
                new RoomNode("middle", new GridPosition(1, 0), RoomType.CORRIDOR),
                new RoomNode("end", new GridPosition(2, 0), RoomType.DEAD_END));
        List<RoomConnection> edges = List.of(
                new RoomConnection("start", "middle", RoomConnectionType.BACKBONE),
                new RoomConnection("middle", "end", RoomConnectionType.BACKBONE));
        return new LevelLayout(42L, 3, 1, rooms, edges);
    }
}
