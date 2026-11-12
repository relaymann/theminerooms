# Architecture

Separate data/catalog, world integration, gameplay systems, and client presentation.

## Planned modules
- **level** — immutable metadata, sourced connections, catalog, validation.
- **world** — deterministic room topology, chunk-slice planning, room-template and palette assignment, bounded decoration assets, dimension data, level-specific chunk generation, structures, transitions.
- **entity** — entity types, sensors, navigation, behavior goals.
- **psychology** — server-authoritative sanity/fear state; clients render synchronized snapshots.
- **survival** — items, resources, hazards, status effects.

## World generation
The topology layer creates an immutable, connected room graph from a seed and a bounded grid configuration. A randomized frontier creates a spanning backbone; optional adjacent edges add loops. After all edges are finalized, non-start rooms are classified by final graph degree. The layout validator requires a connected backbone, cardinally adjacent connections, unique positions/IDs, and bounded dimensions.

Each room occupies a 32x32-block footprint (2x2 Minecraft chunks). ChunkSlicePlanner maps one requested chunk coordinate to a room slice. Doorway metadata is emitted only by the chunk that owns that room boundary. Chunks outside the generated footprint produce no work. An explicit world-chunk origin supports negative and positive world coordinates.

RoomTemplate describes supported room roles, a palette ID, and cardinal openings. RoomTemplateSelector derives required openings from the final room graph and selects deterministically from compatible templates. RoomPalette stores validated namespaced block identifiers for floor, wall, ceiling, and interior material roles. RoomGenerationPlanner validates complete template/palette assignment before chunk generation.

RoomDecoration is a separate bounded immutable asset model for interior decorative blocks. X/Z must stay in 1..30 and Y in 1..6, so assets cannot overwrite the shell or centered perimeter doorways. Duplicate coordinates, malformed block IDs, and assets larger than 4,096 blocks are rejected. RoomDecorationPlanner splits one asset into four immutable chunk slices without changing room-local coordinates. The targeted overload returns only one chunk's work, avoiding whole-room placement during a chunk request.

These classes are planning and validation only. They do not parse resource JSON, resolve block states, load structure NBT, register a dimension, or write decoration blocks. RoomSliceBlockPlacer still has no registered dimension/chunk-generation caller. Integrate world writes only from the target dimension's generation lifecycle, never from a chunk-load event.
