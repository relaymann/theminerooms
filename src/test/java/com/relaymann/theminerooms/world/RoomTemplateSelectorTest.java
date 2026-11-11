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
        RoomNode room = new RoomNode("corridor", new GridPosition(0, 0), RoomType.CORRIDOR);

        assertThrows(IllegalArgumentException.class, () -> selector.select(
                1L, room, Set.of(), List.of(duplicateA, duplicateB)));
        assertThrows(IllegalArgumentException.class,
                () -> selector.select(threeRoomLine(), "not_a_room", List.of(duplicateA)));
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
