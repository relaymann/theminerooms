package com.relaymann.theminerooms.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RoomTemplateSelectorTest {
    private final RoomTemplateSelector selector = new RoomTemplateSelector();

    @Test
    void selectsOnlyTemplatesThatPreserveEveryConnectedDoorway() {
        LevelLayout layout = threeRoomLine();
        RoomTemplate blocked = new RoomTemplate("blocked", Set.of(RoomType.CORRIDOR),
                Set.of(RoomDirection.WEST));
        RoomTemplate valid = new RoomTemplate("valid", Set.of(RoomType.CORRIDOR),
                Set.of(RoomDirection.WEST, RoomDirection.EAST, RoomDirection.NORTH));

        RoomTemplate selected = selector.select(layout, "middle", List.of(blocked, valid));

        assertEquals("valid", selected.id());
        assertTrue(selected.openings().containsAll(Set.of(RoomDirection.WEST, RoomDirection.EAST)));
    }

    @Test
    void selectionIsStableAndIndependentOfInputOrder() {
        RoomNode room = new RoomNode("corridor_17", new GridPosition(2, 3), RoomType.CORRIDOR);
        List<RoomTemplate> templates = List.of(
                new RoomTemplate("corridor_c", Set.of(RoomType.CORRIDOR), Set.of()),
                new RoomTemplate("corridor_a", Set.of(RoomType.CORRIDOR), Set.of()),
                new RoomTemplate("corridor_b", Set.of(RoomType.CORRIDOR), Set.of()));

        RoomTemplate first = selector.select(987654321L, room, Set.of(), templates);
        RoomTemplate second = selector.select(987654321L, room, Set.of(),
                List.of(templates.get(2), templates.get(0), templates.get(1)));

        assertEquals(first, second);
    }

    @Test
    void rejectsWhenNoTemplateCanPreserveRequiredOpenings() {
        RoomNode room = new RoomNode("junction", new GridPosition(0, 0), RoomType.JUNCTION);
        RoomTemplate incompatible = new RoomTemplate("one_way", Set.of(RoomType.JUNCTION),
                Set.of(RoomDirection.NORTH));

        assertThrows(IllegalArgumentException.class, () -> selector.select(
                1L, room, Set.of(RoomDirection.NORTH, RoomDirection.SOUTH), List.of(incompatible)));
    }

    @Test
    void rejectsDuplicateTemplateIdsAndUnknownRooms() {
        RoomTemplate duplicateA = new RoomTemplate("same", Set.of(RoomType.CORRIDOR), Set.of());
        RoomTemplate duplicateB = new RoomTemplate("same", Set.of(RoomType.CORRIDOR), Set.of());
        assertThrows(IllegalArgumentException.class, () -> selector.select(
                1L, new RoomNode("corridor", new GridPosition(0, 0), RoomType.CORRIDOR),
                Set.of(), List.of(duplicateA, duplicateB)));
        assertThrows(IllegalArgumentException.class,
                () -> selector.select(threeRoomLine(), "not_a_room", List.of(duplicateA)));
    }

    @Test
    void batchSelectionCoversEveryRoomAndIsStable() {
        LevelLayout layout = threeRoomLine();
        List<RoomTemplate> templates = List.of(
                new RoomTemplate("start", Set.of(RoomType.START), Set.of(RoomDirection.EAST)),
                new RoomTemplate("corridor", Set.of(RoomType.CORRIDOR),
                        Set.of(RoomDirection.WEST, RoomDirection.EAST)),
                new RoomTemplate("dead_end", Set.of(RoomType.DEAD_END), Set.of(RoomDirection.WEST)));

        var first = selector.selectAll(layout, templates);
        var second = selector.selectAll(layout, List.of(templates.get(2), templates.get(0), templates.get(1)));

        assertEquals(first, second);
        assertEquals(Set.of("start", "middle", "end"), first.keySet());
    }

    @Test
    void batchSelectionPreservesLoopOpeningsAsWellAsBackboneOpenings() {
        List<RoomNode> rooms = List.of(
                new RoomNode("a", new GridPosition(0, 0), RoomType.CORRIDOR),
                new RoomNode("b", new GridPosition(1, 0), RoomType.CORRIDOR),
                new RoomNode("c", new GridPosition(0, 1), RoomType.CORRIDOR),
                new RoomNode("d", new GridPosition(1, 1), RoomType.CORRIDOR));
        List<RoomConnection> edges = List.of(
                new RoomConnection("a", "b", RoomConnectionType.BACKBONE),
                new RoomConnection("a", "c", RoomConnectionType.BACKBONE),
                new RoomConnection("b", "d", RoomConnectionType.BACKBONE),
                new RoomConnection("c", "d", RoomConnectionType.LOOP));
        LevelLayout square = new LevelLayout(99L, 2, 2, rooms, edges);
        RoomTemplate junction = new RoomTemplate("corridor_all_sides", Set.of(RoomType.CORRIDOR),
                Set.of(RoomDirection.NORTH, RoomDirection.EAST, RoomDirection.SOUTH, RoomDirection.WEST));

        var assignments = selector.selectAll(square, List.of(junction));

        assertEquals("corridor_all_sides", assignments.get("a").id());
        RoomTemplate missingLoopOpening = new RoomTemplate("north_only", Set.of(RoomType.CORRIDOR),
                Set.of(RoomDirection.NORTH));
        assertThrows(IllegalArgumentException.class,
                () -> selector.select(square, "c", List.of(missingLoopOpening)));
    }

    private static LevelLayout threeRoomLine() {
        List<RoomNode> rooms = List.of(
                new RoomNode("start", new GridPosition(0, 0), RoomType.START),
                new RoomNode("middle", new GridPosition(1, 0), RoomType.CORRIDOR),
                new RoomNode("end", new GridPosition(2, 0), RoomType.DEAD_END));
        List<RoomConnection> edges = List.of(
                new RoomConnection("start", "middle", RoomConnectionType.BACKBONE),
                new RoomConnection("middle", "end", RoomConnectionType.BACKBONE));
        return new LevelLayout(1L, 3, 1, rooms, edges);
    }
}
