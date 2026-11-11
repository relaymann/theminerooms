package com.relaymann.theminerooms.world;

/** Integer room-grid coordinate; x grows east and z grows south. */
public record GridPosition(int x, int z) {
    public GridPosition {
        if (x < 0 || z < 0) {
            throw new IllegalArgumentException("Grid coordinates must be non-negative");
        }
    }
}
