package com.relaymann.theminerooms.world;

import java.util.Objects;
import java.util.Set;

/**
 * Metadata for an authored 32x32-block room template.
 *
 * This is deliberately independent of Minecraft block states and structure loading.
 * The opening set declares every doorway the template can physically support.
 */
public record RoomTemplate(String id, Set<RoomType> supportedRoomTypes,
                           Set<RoomDirection> openings) {
    public RoomTemplate {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Template id must not be blank");
        }
        id = id.trim();
        if (!id.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException(
                    "Template id must use lowercase letters, digits, '.', '_' or '-'");
        }
        supportedRoomTypes = Set.copyOf(
                Objects.requireNonNull(supportedRoomTypes, "supportedRoomTypes"));
        openings = Set.copyOf(Objects.requireNonNull(openings, "openings"));
        if (supportedRoomTypes.isEmpty()) {
            throw new IllegalArgumentException("A template must support at least one room type");
        }
        if (supportedRoomTypes.contains(null) || openings.contains(null)) {
            throw new IllegalArgumentException("Template sets must not contain null values");
        }
    }

    public boolean supports(RoomNode room, Set<RoomDirection> requiredOpenings) {
        Objects.requireNonNull(room, "room");
        Objects.requireNonNull(requiredOpenings, "requiredOpenings");
        return supportedRoomTypes.contains(room.type()) && openings.containsAll(requiredOpenings);
    }
}
