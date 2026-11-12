package com.relaymann.theminerooms.world;

import java.util.Objects;
import java.util.Set;

/** Immutable per-room assignment of compatible template metadata and its material palette. */
public record RoomBuildSpec(String roomId, RoomTemplate template,
                            RoomPalette palette, Set<RoomDirection> requiredOpenings) {
    public RoomBuildSpec {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("roomId must not be blank");
        }
        roomId = roomId.trim();
        template = Objects.requireNonNull(template, "template");
        palette = Objects.requireNonNull(palette, "palette");
        requiredOpenings = Set.copyOf(Objects.requireNonNull(requiredOpenings, "requiredOpenings"));
        if (!template.paletteId().equals(palette.id())) {
            throw new IllegalArgumentException("Template palette does not match assigned palette");
        }
        if (!template.openings().containsAll(requiredOpenings)) {
            throw new IllegalArgumentException("Template does not preserve every required room opening");
        }
    }
}
