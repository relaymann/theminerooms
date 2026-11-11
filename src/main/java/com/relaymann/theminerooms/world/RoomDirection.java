package com.relaymann.theminerooms.world;

/** Cardinal direction on the room grid; positive Z points south. */
public enum RoomDirection {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0);
    private final int dx;
    private final int dz;
    RoomDirection(int dx, int dz) { this.dx = dx; this.dz = dz; }
    public int dx() { return dx; }
    public int dz() { return dz; }
    public static RoomDirection between(GridPosition from, GridPosition to) {
        int dx = to.x() - from.x();
        int dz = to.z() - from.z();
        for (RoomDirection direction : values()) {
            if (direction.dx == dx && direction.dz == dz) return direction;
        }
        throw new IllegalArgumentException("Rooms must be cardinally adjacent");
    }
}
