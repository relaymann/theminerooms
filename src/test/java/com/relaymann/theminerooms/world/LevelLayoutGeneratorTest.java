package com.relaymann.theminerooms.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LevelLayoutGeneratorTest {
    private final LevelLayoutGenerator generator = new LevelLayoutGenerator();

    @Test
    void sameSeedAndConfigProduceIdenticalLayouts() {
        LevelGenerationConfig config = new LevelGenerationConfig(32, 32, 120, 18);
        assertEquals(generator.generate(8675309L, config), generator.generate(8675309L, config));
    }

    @Test
    void generatedRoomsStayInBoundsAndHaveUniqueIdsAndPositions() {
        LevelLayout layout = generator.generate(42L, new LevelGenerationConfig(9, 7, 40, 5));
        Set<String> ids = new HashSet<>();
        Set<GridPosition> positions = new HashSet<>();
        assertEquals(40, layout.rooms().size());
        assertEquals(1, layout.rooms().stream().filter(room -> room.type() == RoomType.START).count());
        for (RoomNode room : layout.rooms()) {
            assertTrue(room.position().x() >= 0 && room.position().x() < layout.width());
            assertTrue(room.position().z() >= 0 && room.position().z() < layout.depth());
            assertTrue(ids.add(room.id()));
            assertTrue(positions.add(room.position()));
        }
    }

    @Test
    void graphHasConnectedBackboneAndNoMoreThanConfiguredLoops() {
        int maximumLoops = 12;
        LevelLayout layout = generator.generate(1234L,
                new LevelGenerationConfig(20, 20, 100, maximumLoops));
        long backbone = layout.connections().stream()
                .filter(edge -> edge.type() == RoomConnectionType.BACKBONE).count();
        long loops = layout.connections().stream()
                .filter(edge -> edge.type() == RoomConnectionType.LOOP).count();
        assertEquals(layout.rooms().size() - 1, backbone);
        assertTrue(loops <= maximumLoops);
        assertEquals(layout.rooms().size() - 1 + loops, layout.connections().size());
    }

    @Test
    void oneRoomLayoutHasNoConnections() {
        LevelLayout layout = generator.generate(7L, new LevelGenerationConfig(1, 1, 1, 20));
        assertEquals(1, layout.rooms().size());
        assertTrue(layout.connections().isEmpty());
    }

    @Test
    void rejectsImpossibleOrUnboundedConfigurations() {
        assertThrows(IllegalArgumentException.class, () -> new LevelGenerationConfig(0, 4, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new LevelGenerationConfig(129, 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new LevelGenerationConfig(2, 2, 5, 0));
        assertThrows(IllegalArgumentException.class, () -> new LevelGenerationConfig(2, 2, 2, -1));
        assertThrows(IllegalArgumentException.class, () -> new LevelGenerationConfig(128, 128, 1, 0));
    }

    @Test
    void layoutDefensivelyCopiesItsCollections() {
        LevelLayout layout = generator.generate(9L, new LevelGenerationConfig(4, 4, 8, 2));
        assertThrows(UnsupportedOperationException.class, () -> layout.rooms().clear());
        assertThrows(UnsupportedOperationException.class, () -> layout.connections().clear());
        assertFalse(layout.rooms().isEmpty());
    }

    @Test
    void rejectsDisconnectedGraphsEvenWhenTheyHaveEnoughEdges() {
        List<RoomNode> rooms = List.of(
                new RoomNode("a", new GridPosition(0, 0), RoomType.START),
                new RoomNode("b", new GridPosition(1, 0), RoomType.STANDARD),
                new RoomNode("c", new GridPosition(1, 1), RoomType.STANDARD),
                new RoomNode("d", new GridPosition(3, 3), RoomType.STANDARD));
        List<RoomConnection> edges = List.of(
                new RoomConnection("a", "b", RoomConnectionType.BACKBONE),
                new RoomConnection("b", "c", RoomConnectionType.BACKBONE),
                new RoomConnection("c", "a", RoomConnectionType.LOOP));
        assertThrows(IllegalArgumentException.class, () -> new LevelLayout(1L, 4, 4, rooms, edges));
    }

    @Test
    void rejectsDuplicateUndirectedEdgesAndUnknownEndpoints() {
        List<RoomNode> rooms = List.of(
                new RoomNode("a", new GridPosition(0, 0), RoomType.START),
                new RoomNode("b", new GridPosition(1, 0), RoomType.STANDARD));
        List<RoomConnection> duplicateEdges = List.of(
                new RoomConnection("a", "b", RoomConnectionType.BACKBONE),
                new RoomConnection("b", "a", RoomConnectionType.LOOP));
        assertThrows(IllegalArgumentException.class, () -> new LevelLayout(1L, 2, 1, rooms, duplicateEdges));

        List<RoomConnection> unknownEndpoint = List.of(
                new RoomConnection("a", "missing", RoomConnectionType.BACKBONE));
        assertThrows(IllegalArgumentException.class, () -> new LevelLayout(1L, 2, 1, rooms, unknownEndpoint));
    }

    @Test
    void rejectsNonAdjacentRoomEdgesAndAConnectedGraphWithoutAValidBackbone() {
        List<RoomNode> rooms = List.of(
                new RoomNode("a", new GridPosition(0, 0), RoomType.START),
                new RoomNode("b", new GridPosition(2, 0), RoomType.STANDARD));
        List<RoomConnection> nonAdjacent = List.of(
                new RoomConnection("a", "b", RoomConnectionType.BACKBONE));
        assertThrows(IllegalArgumentException.class, () -> new LevelLayout(1L, 3, 1, rooms, nonAdjacent));

        List<RoomNode> adjacentRooms = List.of(
                new RoomNode("a", new GridPosition(0, 0), RoomType.START),
                new RoomNode("b", new GridPosition(1, 0), RoomType.STANDARD));
        List<RoomConnection> loopOnly = List.of(
                new RoomConnection("a", "b", RoomConnectionType.LOOP));
        assertThrows(IllegalArgumentException.class, () -> new LevelLayout(1L, 2, 1, adjacentRooms, loopOnly));
    }
}
