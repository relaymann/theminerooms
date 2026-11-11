# Architecture

## Boundaries

Separate data/catalog, world integration, gameplay systems, and client presentation. Common code must load on a dedicated server without touching client-only classes.

## Planned modules

- **level** — immutable metadata, sourced connections, catalog, validation.
- **world** — dimension data, level-specific chunk generation, structures, transitions.
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

1. Authored landmarks preserve recognizable layouts.
2. Procedural room/corridor graph assembly creates variation.
3. Connectivity validation ensures generated paths are navigable.
4. Transition points are placed only for documented connections.
5. Seeds and generation version are stored for reproducibility.

Keep generation bounded and chunk-local. Avoid whole-level synchronous generation and unbounded structure searches.

## Persistence

Use versioned SavedData for world discoveries and server state. Use versioned player persistent data for player-specific state. Every format needs a schema version and migration path.

## Performance

Avoid per-tick global scans; use event-driven updates and staggered AI perception. Cache immutable metadata, bound path planning, and do not generate distant chunks merely to validate transitions. Profile dedicated-server behavior with multiple players.
