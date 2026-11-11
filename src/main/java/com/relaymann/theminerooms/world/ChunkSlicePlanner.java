package com.relaymann.theminerooms.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Maps requested Minecraft chunks to room slices in constant time after construction.
 * One room occupies a 2x2 chunk area (32x32 blocks). This planner never loads or writes chunks.
 */
public final class ChunkSlicePlanner {
    public static final int CHUNK_SIZE = 16;
    public static final int ROOM_SIZE_IN_CHUNKS = 2;

    private final Map<GridPosition, RoomNode> roomsByPosition;
    private final int originChunkX;
    private final int originChunkZ;
    private final Map<String, List<RoomOpening>> openingsByRoomId;

    public ChunkSlicePlanner(LevelLayout layout) {
        this(layout, 0, 0);
    }

    /**
     * Creates a planner whose grid position (0, 0) begins at the supplied world chunk.
     * This allows bounded layouts to be centered around negative or positive world coordinates.
     */
    public ChunkSlicePlanner(LevelLayout layout, int originChunkX, int originChunkZ) {
        if (layout == null) {
            throw new IllegalArgumentException("layout must not be null");
        }

        Map<GridPosition, RoomNode> rooms = new HashMap<>();
        Map<String, RoomNode> roomsById = new HashMap<>();
        Map<String, List<RoomOpening>> openings = new HashMap<>();
        for (RoomNode room : layout.rooms()) {
            rooms.put(room.position(), room);
            roomsById.put(room.id(), room);
            openings.put(room.id(), new ArrayList<>());
        }
        for (RoomConnection connection : layout.connections()) {
            RoomNode from = roomsById.get(connection.fromRoomId());
            RoomNode to = roomsById.get(connection.toRoomId());
            RoomDirection fromDirection = RoomDirection.between(from.position(), to.position());
            RoomDirection toDirection = RoomDirection.between(to.position(), from.position());
            openings.get(from.id()).add(new RoomOpening(fromDirection, to.id(), connection.type()));
            openings.get(to.id()).add(new RoomOpening(toDirection, from.id(), connection.type()));
        }

        this.roomsByPosition = Map.copyOf(rooms);
        this.originChunkX = originChunkX;
        this.originChunkZ = originChunkZ;
        Map<String, List<RoomOpening>> immutableOpenings = new HashMap<>();
        openings.forEach((id, values) -> immutableOpenings.put(id, List.copyOf(values)));
        this.openingsByRoomId = Map.copyOf(immutableOpenings);
    }

    public ChunkWorkPlan plan(int chunkX, int chunkZ) {
        long relativeChunkX = (long) chunkX - originChunkX;
        long relativeChunkZ = (long) chunkZ - originChunkZ;
        long roomX = Math.floorDiv(relativeChunkX, ROOM_SIZE_IN_CHUNKS);
        long roomZ = Math.floorDiv(relativeChunkZ, ROOM_SIZE_IN_CHUNKS);
        if (roomX < 0 || roomZ < 0 || roomX > Integer.MAX_VALUE || roomZ > Integer.MAX_VALUE) {
            return new ChunkWorkPlan(chunkX, chunkZ, Optional.empty());
        }

        int localX = (int) Math.floorMod(relativeChunkX, ROOM_SIZE_IN_CHUNKS);
        int localZ = (int) Math.floorMod(relativeChunkZ, ROOM_SIZE_IN_CHUNKS);
        RoomNode room = roomsByPosition.get(new GridPosition((int) roomX, (int) roomZ));
        if (room == null) {
            return new ChunkWorkPlan(chunkX, chunkZ, Optional.empty());
        }

        List<RoomOpening> visibleOpenings = openingsByRoomId.get(room.id()).stream()
                .filter(opening -> isOnThisChunkBoundary(opening.direction(), localX, localZ))
                .toList();
        RoomChunkSlice slice = new RoomChunkSlice(
                room.id(), room.type(), localX, localZ, visibleOpenings);
        return new ChunkWorkPlan(chunkX, chunkZ, Optional.of(slice));
    }

    private static boolean isOnThisChunkBoundary(RoomDirection direction, int localX, int localZ) {
        return switch (direction) {
            case NORTH -> localZ == 0;
            case EAST -> localX == 1;
            case SOUTH -> localZ == 1;
            case WEST -> localX == 0;
        };
    }
}
