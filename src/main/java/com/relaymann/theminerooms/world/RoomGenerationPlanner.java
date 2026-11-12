package com.relaymann.theminerooms.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Builds a complete immutable room/template/palette plan before chunk generation begins. */
public final class RoomGenerationPlanner {
    private final RoomTemplateSelector templateSelector = new RoomTemplateSelector();

    public RoomGenerationPlan plan(LevelLayout layout, List<RoomTemplate> templates,
                                   List<RoomPalette> palettes) {
        return plan(layout, templates, palettes, 0, 0);
    }

    public RoomGenerationPlan plan(LevelLayout layout, List<RoomTemplate> templates,
                                   List<RoomPalette> palettes,
                                   int originChunkX, int originChunkZ) {
        Objects.requireNonNull(layout, "layout");
        Objects.requireNonNull(templates, "templates");
        Objects.requireNonNull(palettes, "palettes");

        Map<String, RoomPalette> palettesById = new HashMap<>();
        for (RoomPalette palette : palettes) {
            Objects.requireNonNull(palette, "palettes must not contain null");
            if (palettesById.putIfAbsent(palette.id(), palette) != null) {
                throw new IllegalArgumentException("Duplicate room palette id: " + palette.id());
            }
        }

        Map<String, RoomTemplate> selectedTemplates = templateSelector.selectAll(layout, templates);
        Map<String, RoomNode> roomsById = new HashMap<>();
        Map<String, java.util.Set<RoomDirection>> openingsByRoom = new HashMap<>();
        for (RoomNode room : layout.rooms()) {
            roomsById.put(room.id(), room);
            openingsByRoom.put(room.id(), new java.util.HashSet<>());
        }
        for (RoomConnection connection : layout.connections()) {
            RoomNode from = roomsById.get(connection.fromRoomId());
            RoomNode to = roomsById.get(connection.toRoomId());
            openingsByRoom.get(from.id()).add(RoomDirection.between(from.position(), to.position()));
            openingsByRoom.get(to.id()).add(RoomDirection.between(to.position(), from.position()));
        }

        Map<String, RoomBuildSpec> assignments = new HashMap<>();
        for (RoomNode room : layout.rooms()) {
            RoomTemplate template = selectedTemplates.get(room.id());
            RoomPalette palette = palettesById.get(template.paletteId());
            if (palette == null) {
                throw new IllegalArgumentException("Template " + template.id()
                        + " references missing palette: " + template.paletteId());
            }
            assignments.put(room.id(), new RoomBuildSpec(
                    room.id(), template, palette, openingsByRoom.get(room.id())));
        }

        return new RoomGenerationPlan(
                new ChunkSlicePlanner(layout, originChunkX, originChunkZ), assignments);
    }
}
