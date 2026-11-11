package com.relaymann.theminerooms.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Deterministic, bounded room-graph generator.
 *
 * This creates topology only. Minecraft blocks, chunks, structures, transitions, and
 * authored landmark templates are deliberately handled by later world-integration layers.
 */
public final class LevelLayoutGenerator {
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public LevelLayout generate(long seed, LevelGenerationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }

        Random random = new Random(seed);
        List<RoomNode> rooms = new ArrayList<>(config.roomCount());
        List<RoomConnection> connections = new ArrayList<>(config.roomCount() - 1);
        Map<GridPosition, RoomNode> occupied = new HashMap<>();
        List<FrontierLink> frontier = new ArrayList<>();

        GridPosition start = new GridPosition(config.width() / 2, config.depth() / 2);
        RoomNode startRoom = createRoom(start, RoomType.START);
        rooms.add(startRoom);
        occupied.put(start, startRoom);
        addFrontier(start, config, occupied, frontier);

        while (rooms.size() < config.roomCount()) {
            if (frontier.isEmpty()) {
                throw new IllegalStateException("Room frontier exhausted before requested room count");
            }
            FrontierLink link = frontier.remove(random.nextInt(frontier.size()));
            if (occupied.containsKey(link.target())) {
                continue;
            }

            RoomNode room = createRoom(link.target(), RoomType.STANDARD);
            occupied.put(link.target(), room);
            rooms.add(room);
            connections.add(new RoomConnection(
                    occupied.get(link.source()).id(), room.id(), RoomConnectionType.BACKBONE));
            addFrontier(link.target(), config, occupied, frontier);
        }

        addLoops(random, config.maxExtraConnections(), occupied, connections);
        return new LevelLayout(seed, config.width(), config.depth(), rooms, connections);
    }

    private static RoomNode createRoom(GridPosition position, RoomType type) {
        return new RoomNode("room_" + position.x() + "_" + position.z(), position, type);
    }

    private static void addFrontier(GridPosition source, LevelGenerationConfig config,
                                    Map<GridPosition, RoomNode> occupied, List<FrontierLink> frontier) {
        for (int[] direction : DIRECTIONS) {
            int x = source.x() + direction[0];
            int z = source.z() + direction[1];
            if (x >= 0 && z >= 0 && x < config.width() && z < config.depth()) {
                GridPosition target = new GridPosition(x, z);
                if (!occupied.containsKey(target)) {
                    frontier.add(new FrontierLink(source, target));
                }
            }
        }
    }

    private static void addLoops(Random random, int maximum, Map<GridPosition, RoomNode> occupied,
                                 List<RoomConnection> connections) {
        if (maximum == 0 || occupied.size() < 3) {
            return;
        }

        Set<String> existing = new HashSet<>();
        for (RoomConnection connection : connections) {
            existing.add(connection.undirectedKey());
        }

        List<RoomConnection> candidates = new ArrayList<>();
        List<RoomNode> orderedRooms = occupied.values().stream()
                .sorted(Comparator.comparingInt((RoomNode room) -> room.position().x())
                        .thenComparingInt(room -> room.position().z()))
                .toList();
        for (RoomNode room : orderedRooms) {
            GridPosition position = room.position();
            for (int[] direction : new int[][]{{1, 0}, {0, 1}}) {
                RoomNode neighbor = occupied.get(new GridPosition(position.x() + direction[0],
                        position.z() + direction[1]));
                if (neighbor != null) {
                    RoomConnection candidate = new RoomConnection(
                            room.id(), neighbor.id(), RoomConnectionType.LOOP);
                    if (!existing.contains(candidate.undirectedKey())) {
                        candidates.add(candidate);
                    }
                }
            }
        }

        // Random's shuffle sequence is stable for a given seed on supported Java runtimes.
        java.util.Collections.shuffle(candidates, random);
        int count = Math.min(maximum, candidates.size());
        connections.addAll(candidates.subList(0, count));
    }

    private record FrontierLink(GridPosition source, GridPosition target) { }
}
