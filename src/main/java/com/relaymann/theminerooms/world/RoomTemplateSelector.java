package com.relaymann.theminerooms.world;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Selects compatible authored room-template metadata deterministically. */
public final class RoomTemplateSelector {
    private static final long FNV_OFFSET_BASIS = 0xcbf29ce484222325L;
    private static final long FNV_PRIME = 0x100000001b3L;

    /**
     * Selects a template for one room in a validated layout. Every graph edge incident
     * to the room becomes a required doorway, so a selected template cannot seal a route.
     */
    public RoomTemplate select(LevelLayout layout, String roomId, List<RoomTemplate> templates) {
        Objects.requireNonNull(layout, "layout");
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("roomId must not be blank");
        }

        RoomNode room = layout.rooms().stream()
                .filter(candidate -> candidate.id().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown room: " + roomId));

        Set<RoomDirection> requiredOpenings = new HashSet<>();
        for (RoomConnection connection : layout.connections()) {
            if (connection.fromRoomId().equals(roomId)) {
                RoomNode target = findRoom(layout, connection.toRoomId());
                requiredOpenings.add(RoomDirection.between(room.position(), target.position()));
            } else if (connection.toRoomId().equals(roomId)) {
                RoomNode source = findRoom(layout, connection.fromRoomId());
                requiredOpenings.add(RoomDirection.between(room.position(), source.position()));
            }
        }
        return select(layout.seed(), room, requiredOpenings, templates);
    }

    /**
     * Selects from templates that match the room role and contain all required openings.
     * Candidate ordering supplied by callers does not affect the result.
     */
    public RoomTemplate select(long levelSeed, RoomNode room,
                               Set<RoomDirection> requiredOpenings,
                               List<RoomTemplate> templates) {
        Objects.requireNonNull(room, "room");
        Objects.requireNonNull(requiredOpenings, "requiredOpenings");
        Objects.requireNonNull(templates, "templates");
        if (requiredOpenings.contains(null)) {
            throw new IllegalArgumentException("requiredOpenings must not contain null");
        }

        Set<String> ids = new HashSet<>();
        List<RoomTemplate> compatible = new ArrayList<>();
        for (RoomTemplate template : templates) {
            Objects.requireNonNull(template, "templates must not contain null");
            if (!ids.add(template.id())) {
                throw new IllegalArgumentException("Duplicate room template id: " + template.id());
            }
            if (template.supports(room, requiredOpenings)) {
                compatible.add(template);
            }
        }
        if (compatible.isEmpty()) {
            throw new IllegalArgumentException("No room template supports role " + room.type()
                    + " and openings " + requiredOpenings + " for room " + room.id());
        }

        compatible.sort(Comparator.comparing(RoomTemplate::id));
        long stableRoomHash = stableHash(room.id());
        long selectionBits = mix64(levelSeed ^ stableRoomHash);
        int index = (int) Long.remainderUnsigned(selectionBits, compatible.size());
        return compatible.get(index);
    }

    private static RoomNode findRoom(LevelLayout layout, String id) {
        return layout.rooms().stream()
                .filter(room -> room.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Layout connection references missing room: " + id));
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
