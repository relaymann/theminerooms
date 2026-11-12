package com.relaymann.theminerooms.world;

import java.util.Objects;

/**
 * Namespaced block identifiers for a room's four base material roles.
 * Identifiers are resolved to Minecraft BlockStates by the later world adapter.
 */
public record RoomPalette(String id, String floorBlock, String wallBlock,
                          String ceilingBlock, String interiorBlock) {
    private static final String RESOURCE_ID = "[a-z0-9_.-]+:[a-z0-9_./-]+";

    public RoomPalette {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Palette id must not be blank");
        }
        id = id.trim();
        if (!id.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("Palette id must use lowercase letters, digits, '.', '_' or '-'");
        }
        floorBlock = validateBlockId(floorBlock, "floorBlock");
        wallBlock = validateBlockId(wallBlock, "wallBlock");
        ceilingBlock = validateBlockId(ceilingBlock, "ceilingBlock");
        interiorBlock = validateBlockId(interiorBlock, "interiorBlock");
    }

    private static String validateBlockId(String value, String field) {
        Objects.requireNonNull(value, field);
        String normalized = value.trim();
        if (!normalized.matches(RESOURCE_ID)) {
            throw new IllegalArgumentException(field + " must be a namespaced block id");
        }
        return normalized;
    }
}
