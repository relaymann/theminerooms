package com.relaymann.theminerooms.world;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Immutable, validated room graph. It contains no Minecraft world or chunk references. */
public record LevelLayout(long seed, int width, int depth,
                          List<RoomNode> rooms, List<RoomConnection> connections) {
    public LevelLayout {
        if (width < 1 || depth < 1 || width > 128 || depth > 128
                || (long) width * depth > 4_096) {
            throw new IllegalArgumentException("Layout dimensions exceed supported generation bounds");
        }
        rooms = List.copyOf(Objects.requireNonNull(rooms, "rooms"));
        connections = List.copyOf(Objects.requireNonNull(connections, "connections"));
        if (rooms.isEmpty()) {
            throw new IllegalArgumentException("A layout must contain at least one room");
        }

        Map<String, RoomNode> byId = new HashMap<>();
        Set<GridPosition> positions = new HashSet<>();
        for (RoomNode room : rooms) {
            if (room.position().x() >= width || room.position().z() >= depth) {
                throw new IllegalArgumentException("Room is outside layout bounds: " + room.id());
            }
            if (byId.putIfAbsent(room.id(), room) != null) {
                throw new IllegalArgumentException("Duplicate room id: " + room.id());
            }
            if (!positions.add(room.position())) {
                throw new IllegalArgumentException("Duplicate room position: " + room.position());
            }
        }

        Set<String> edgeKeys = new HashSet<>();
        Map<String, Set<String>> adjacency = new HashMap<>();
        byId.keySet().forEach(id -> adjacency.put(id, new HashSet<>()));
        for (RoomConnection connection : connections) {
            if (!byId.containsKey(connection.fromRoomId()) || !byId.containsKey(connection.toRoomId())) {
                throw new IllegalArgumentException("Connection references an unknown room");
            }
            if (!edgeKeys.add(connection.undirectedKey())) {
                throw new IllegalArgumentException("Duplicate room connection: " + connection.undirectedKey());
            }
            adjacency.get(connection.fromRoomId()).add(connection.toRoomId());
            adjacency.get(connection.toRoomId()).add(connection.fromRoomId());
        }
        if (rooms.size() > 1 && connections.size() < rooms.size() - 1) {
            throw new IllegalArgumentException("A multi-room layout needs a connected backbone");
        }
        if (!isConnected(rooms.getFirst().id(), adjacency, rooms.size())) {
            throw new IllegalArgumentException("Room graph is disconnected");
        }
    }

    private static boolean isConnected(String start, Map<String, Set<String>> adjacency, int expectedSize) {
        Set<String> visited = new HashSet<>();
        ArrayDeque<String> pending = new ArrayDeque<>();
        pending.add(start);
        while (!pending.isEmpty()) {
            String current = pending.removeFirst();
            if (visited.add(current)) {
                pending.addAll(adjacency.get(current));
            }
        }
        return visited.size() == expectedSize;
    }
}
