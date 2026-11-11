package com.relaymann.theminerooms.world;

/** Hard bounds for one in-memory room topology generation request. */
public record LevelGenerationConfig(int width, int depth, int roomCount, int maxExtraConnections) {
    private static final int MAX_GRID_SIDE = 128;
    private static final int MAX_GRID_CELLS = 4_096;

    public LevelGenerationConfig {
        if (width < 1 || depth < 1 || width > MAX_GRID_SIDE || depth > MAX_GRID_SIDE) {
            throw new IllegalArgumentException("Grid dimensions must be between 1 and " + MAX_GRID_SIDE);
        }
        if ((long) width * depth > MAX_GRID_CELLS) {
            throw new IllegalArgumentException("Grid may contain at most " + MAX_GRID_CELLS + " cells");
        }
        if (roomCount < 1 || roomCount > width * depth) {
            throw new IllegalArgumentException("roomCount must fit inside the configured grid");
        }
        if (maxExtraConnections < 0) {
            throw new IllegalArgumentException("maxExtraConnections must not be negative");
        }
    }
}
