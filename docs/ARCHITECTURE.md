# Architecture

## Boundaries

Separate data/catalog, world integration, gameplay systems, and client presentation. Common code must load on a dedicated server without touching client-only classes.

## Planned modules

- **level** — immutable metadata, sourced connections, catalog, validation.
- **world** — deterministic room topology, chunk-slice planning, room-template and palette assignment, dimension data, level-specific chunk generation, structures, transitions.
- **entity** — entity types, sensors, navigation, behavior goals.
- **psychology** — server-authoritative sanity/fear state; clients render synchronized snapshots.
- **survival** — items, resources, hazards, status effects.
- **arg** — clue records, discovery state, puzzle evaluation.
- **network** — validated payloads for synchronized state and player actions.
- **client** — screens, audio, visual effects.

## Authority

The logical server owns transitions, entity decisions, survival consequences, clue completion, and persistent state. Clients render synchronized state and send validated requests. Player-specific hallucinations are local presentation and must not mutate shared blocks or authoritative world state. Shared environmental events are explicit server events.

## Level data

Every released level must pass `LevelDataValidator`: one or two sourced entrances, one or two sourced exits, known destinations, correct direction labels, unique connection IDs, and individual Fandom Wiki source pages. Draft data may remain incomplete outside release data.

## World generation

The topology layer creates an immutable, connected room graph from a seed and a bounded grid configuration. A randomized frontier creates a spanning backbone; optional adjacent edges add loops. After all edges are finalized, non-start rooms are classified by final graph degree as dead ends (degree 1), corridors (degree 2), or junctions (degree 3+). These semantic roles distinguish room shapes without changing connectivity or inventing lore landmarks. The layout validator requires exactly rooms minus one backbone edges, a connected backbone, cardinally adjacent connections, unique positions/IDs, and bounded dimensions.

Each room occupies a 32x32-block footprint (2x2 Minecraft chunks). `ChunkSlicePlanner` converts one requested chunk coordinate into a small immutable slice description in constant time after its indexes are built. Doorway metadata is emitted only by the chunk that owns that room boundary. Chunks outside the generated footprint produce no work. The planner accepts an explicit world-chunk origin for grid position (0, 0), so bounded layouts can be placed across negative and positive world coordinates without changing their deterministic topology.

`RoomTemplate` describes supported room roles, a palette ID, and the cardinal openings an authored template can contain. `RoomTemplateSelector.selectAll` derives all required openings from the final room graph in one pass, filters templates that would seal any route, and makes deterministic assignments from the level seed and stable room IDs. `RoomPalette` stores validated namespaced block identifiers for floor, wall, ceiling, and interior material roles. `RoomGenerationPlanner` validates that every room has a compatible template and palette before returning an immutable `RoomGenerationPlan`; its chunk queries pair the existing slice plan with the room's selected template and palette.

These classes are metadata and planning only. They do not load structure NBT, resolve block states, register a dimension, or write blocks. `RoomSliceBlockPlacer` still accepts caller-supplied block states and has no registered dimension/chunk-generation caller. Integrate it only from the target dimension's generation lifecycle, never from a chunk-load event. Documented transitions are attached only from validated level connection data.

Generation limits are enforced before allocation (maximum 128 cells per side and 4,096 grid cells). Avoid whole-level synchronous block generation, distant-chunk searches, and unbounded structure scans.

## Persistence

Use versioned SavedData for world discoveries and server state. Use versioned player persistent data for player-specific state. Every format needs a schema version and migration path.

## Performance

Avoid per-tick global scans; use event-driven updates and staggered AI perception. Cache immutable metadata, bound path planning, and do not generate distant chunks merely to validate transitions. Profile dedicated-server behavior with multiple players.
