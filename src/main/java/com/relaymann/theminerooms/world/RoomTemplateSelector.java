package com.relaymann.theminerooms.world;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Selects compatible authored room-template metadata deterministically. */
public final class RoomTemplateSelector {
    private static final long FNV_OFFSET_BASIS = 0xcbf29ce484222325L;
    private static final long FNV_PRIME = 0x100000001b3L;

    /**
     * Selects templates for every room in one pass over the graph.
     * A selected template must expose every edge incident to its room.
     */
    public Map<String, RoomTemplate> selectAll(LevelLayout layout, List<RoomTemplate> templates) {
        Objects.requireNonNull(layout, "layout");
        Objects.requireNonNull(templates, "templates");

        Map<String, RoomNode> roomsById = new HashMap<>();
        Map<String, Set<RoomDirection>> openingsByRoom = new HashMap<>();
        for (RoomNode room : layout.rooms()) {
            roomsById.put(room.id(), room);
            openingsByRoom.put(room.id(), new HashSet<>());
        }

        for (RoomConnection connection : layout.connections()) {
            RoomNode from = roomsById.get(connection.fromRoomId());
            RoomNode to = roomsById.get(connection.toRoomId());
            if (from == null || to == null) {
                throw new IllegalArgumentException("Connection references an unknown room");
            }
            openingsByRoom.get(from.id()).add(RoomDirection.between(from.position(), to.position()));
            openingsByRoom.get(to.id()).add(RoomDirection.between(to.position(), from.position()));
        }

        Set<String> templateIds = new HashSet<>();
        Map<RoomType, List<RoomTemplate>> candidatesByType = new HashMap<>();
        for (RoomTemplate template : templates) {
            Objects.requireNonNull(template, "templates must not contain null");
            if (!templateIds.add(template.id())) {
                throw new IllegalArgumentException("Duplicate room template id: " + template.id());
            }
            for (RoomType type : template.supportedRoomTypes()) {
                candidatesByType.computeIfAbsent(type, ignored -> new ArrayList<>()).add(template);
            }
        }
        candidatesByType.values().forEach(list -> list.sort(Comparator.comparing(RoomTemplate::id)));

        Map<String, RoomTemplate> assignments = new LinkedHashMap<>();
        for (RoomNode room : layout.rooms()) {
            List<RoomTemplate> compatible = candidatesByType.getOrDefault(room.type(), List.of())
                    .stream()
                    .filter(template -> template.openings().containsAll(openingsByRoom.get(room.id())))
                    .toList();
            if (compatible.isEmpty()) {
                throw new IllegalArgumentException("No room template supports role " + room.type()
                        + " and openings " + openingsByRoom.get(room.id()) + " for room " + room.id());
            }
            long selectionBits = mix64(layout.seed() ^ stableHash(room.id()));
            int index = (int) Long.remainderUnsigned(selectionBits, compatible.size());
            assignments.put(room.id(), compatible.get(index));
        }
        return java.util.Collections.unmodifiableMap(assignments);
    }

    /** Convenience method for one room; batch callers should use selectAll. */
    public RoomTemplate select(LevelLayout layout, String roomId, List<RoomTemplate> templates) {
        Objects.requireNonNull(layout, "layout");
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("roomId must not be blank");
        }
        String normalizedId = roomId.trim();
        RoomNode target = layout.rooms().stream()
                .filter(room -> room.id().equals(normalizedId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown room: " + normalizedId));
        Map<String, RoomNode> roomsById = new HashMap<>();
        for (RoomNode room : layout.rooms()) {
            roomsById.put(room.id(), room);
        }
        Set<RoomDirection> requiredOpenings = new HashSet<>();
        for (RoomConnection connection : layout.connections()) {
            if (connection.fromRoomId().equals(normalizedId)) {
                RoomNode neighbor = roomsById.get(connection.toRoomId());
                requiredOpenings.add(RoomDirection.between(target.position(), neighbor.position()));
            } else if (connection.toRoomId().equals(normalizedId)) {
                RoomNode neighbor = roomsById.get(connection.fromRoomId());
                requiredOpenings.add(RoomDirection.between(target.position(), neighbor.position()));
            }
        }
        return select(layout.seed(), target, requiredOpenings, templates);
    }

    /** Selects a template from an explicitly supplied, already-derived opening set. */
    public RoomTemplate select(long levelSeed, RoomNode room,
                               Set<RoomDirection> requiredOpenings,
                               List<RoomTemplate> templates) {
        Objects.requireNonNull(room, "room");
        Set<RoomDirection> safeOpenings = Set.copyOf(
                Objects.requireNonNull(requiredOpenings, "requiredOpenings"));
        Objects.requireNonNull(templates, "templates");

        Set<String> ids = new HashSet<>();
        List<RoomTemplate> compatible = new ArrayList<>();
        for (RoomTemplate template : templates) {
            Objects.requireNonNull(template, "templates must not contain null");
            if (!ids.add(template.id())) {
                throw new IllegalArgumentException("Duplicate room template id: " + template.id());
            }
            if (template.supports(room, safeOpenings)) {
                compatible.add(template);
            }
        }
        if (compatible.isEmpty()) {
            throw new IllegalArgumentException("No room template supports role " + room.type()
                    + " and openings " + safeOpenings + " for room " + room.id());
        }
        compatible.sort(Comparator.comparing(RoomTemplate::id));
        int index = (int) Long.remainderUnsigned(
                mix64(levelSeed ^ stableHash(room.id())), compatible.size());
        return compatible.get(index);
    }

    private static long stableHash(String value) {
        long hash = FNV_OFFSET_BASIS;
        for (byte character : value.getBytes(StandardCharsets.UTF_8)) {
            hash ^= character & 0xffL;
            hash *= FNV_PRIME;
        }
        return hash;
    }

    private static long mix64(long value) {
        value = (value ^ (value >>> 30)) * 0xbf58476d1ce4e5b9L;
        value = (value ^ (value >>> 27)) * 0x94d049bb133111ebL;
        return value ^ (value >>> 31);
    }
}
