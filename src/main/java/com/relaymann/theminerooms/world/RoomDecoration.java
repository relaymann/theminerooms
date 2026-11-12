package com.relaymann.theminerooms.world;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/** Immutable, bounded decorative asset. Only interior coordinates are accepted. */
public record RoomDecoration(String id, List<RoomDecorationBlock> blocks) {
    public static final int MAX_BLOCKS = 4096;
    public RoomDecoration {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Decoration id must not be blank");
        id = id.trim();
        if (!id.matches("[a-z0-9_.-]+"))
            throw new IllegalArgumentException("Decoration id must use lowercase letters, digits, '.', '_' or '-'");
        blocks = List.copyOf(Objects.requireNonNull(blocks, "blocks"));
        if (blocks.size() > MAX_BLOCKS)
            throw new IllegalArgumentException("Decoration exceeds the " + MAX_BLOCKS + " block safety limit");
        Set<Long> occupied = new HashSet<>();
        for (RoomDecorationBlock block : blocks) {
            Objects.requireNonNull(block, "decoration block");
            long key = ((long) block.y() << 10) | ((long) block.z() << 5) | block.x();
            if (!occupied.add(key)) throw new IllegalArgumentException("Decoration contains duplicate coordinates");
        }
    }
}
