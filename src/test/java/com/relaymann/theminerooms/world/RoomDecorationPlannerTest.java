package com.relaymann.theminerooms.world;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
class RoomDecorationPlannerTest {
    private final RoomDecorationPlanner planner = new RoomDecorationPlanner();
    @Test void splitsDecorationAcrossAllFourChunks() {
        RoomDecoration decoration = new RoomDecoration("starter", List.of(
                block(1,2,1), block(17,2,1), block(1,3,17), block(30,6,30)));
        List<RoomDecorationSlice> slices = planner.plan(decoration);
        assertEquals(4, slices.size());
        assertEquals(List.of(block(1,2,1)), slices.get(0).blocks());
        assertEquals(List.of(block(17,2,1)), slices.get(1).blocks());
        assertEquals(List.of(block(1,3,17)), slices.get(2).blocks());
        assertEquals(List.of(block(30,6,30)), slices.get(3).blocks());
    }
    @Test void targetedPlanContainsOnlyOwnedBlocks() {
        RoomDecoration decoration = new RoomDecoration("targeted", List.of(block(2,2,2), block(20,2,20)));
        assertEquals(List.of(block(20,2,20)), planner.plan(decoration,1,1).blocks());
    }
    @Test void rejectsPerimeterAndVerticalShellCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> block(0,2,10));
        assertThrows(IllegalArgumentException.class, () -> block(10,2,31));
        assertThrows(IllegalArgumentException.class, () -> block(10,0,10));
        assertThrows(IllegalArgumentException.class, () -> block(10,7,10));
    }
    @Test void rejectsDuplicateCoordinatesAndMalformedBlockIds() {
        RoomDecorationBlock b = block(4,2,4);
        assertThrows(IllegalArgumentException.class, () -> new RoomDecoration("duplicate",List.of(b,b)));
        assertThrows(IllegalArgumentException.class, () -> new RoomDecorationBlock(4,2,4,"stone"));
    }
    @Test void enforcesAssetSizeLimitAndImmutableSlices() {
        List<RoomDecorationBlock> tooMany = java.util.stream.IntStream.range(0,4097)
                .mapToObj(i -> block(1+i%30,1+(i/30)%6,1+(i/180)%30)).toList();
        assertThrows(IllegalArgumentException.class, () -> new RoomDecoration("large",tooMany));
        RoomDecorationSlice empty = planner.plan(new RoomDecoration("empty",List.of()),0,0);
        assertTrue(empty.blocks().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> empty.blocks().add(block(1,1,1)));
    }
    private static RoomDecorationBlock block(int x,int y,int z) {
        return new RoomDecorationBlock(x,y,z,"minecraft:oak_planks");
    }
}
