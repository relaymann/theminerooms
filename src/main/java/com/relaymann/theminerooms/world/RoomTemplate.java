package com.relaymann.theminerooms.world;

import java.util.Objects;
import java.util.Set;

/**
 * Metadata for an authored 32x32-block room template.
 * Structure loading is intentionally kept outside this pure-Java model.
 */
public record RoomTemplate(String id, String paletteId,
                           Set<RoomType> supportedRoomTypes,
                           Set<RoomDirection> openings) {
    /** Compatibility constructor for templates that use the default palette. */
    public RoomTemplate(String id, Set<RoomType> supportedRoomTypes,
                        Set<RoomDirection> openings) {
        this(id, "default", supportedRoomTypes, openings);
    }

    public RoomTemplate {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Template id must not be blank");
        }
        id = id.trim();
        if (!id.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException(
                    "Template id must use lowercase letters, digits, '.', '_' or '-'");
        }
        if (paletteId == null || paletteId.isBlank()) {
            throw new IllegalArgumentException("Template palette id must not be blank");
        }
        paletteId = paletteId.trim();
        if (!paletteId.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("Palette id must use lowercase letters, digits, '.', '_' or '-'");
        }
        supportedRoomTypes = Set.copyOf(
                Objects.requireNonNull(supportedRoomTypes, "supportedRoomTypes"));
        openings = Set.copyOf(Objects.requireNonNull(openings, "openings"));
        if (supportedRoomTypes.isEmpty()) {
            throw new IllegalArgumentException("A template must support at least one room type");
        }
        // Set.copyOf rejects null elements; do not call immutable Set.contains(null), which throws.
    }

    public boolean supports(RoomNode room, Set<RoomDirection> requiredOpenings) {
        Objects.requireNonNull(room, "room");
        Objects.requireNonNull(requiredOpenings, "requiredOpenings");
        return supportedRoomTypes.contains(room.type()) && openings.containsAll(requiredOpenings);
    }
}
