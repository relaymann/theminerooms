# Roadmap
This is a staged plan, not a claim that features are already implemented.

## Foundation
- [x] NeoForge 1.21.1 / 21.1.262 / Java 21 build configuration.
- [x] Dedicated-server-safe mod entry point.
- [x] Typed level and connection model.
- [x] Source and connection-count validation.
- [x] CI build and unit-test workflow.
- [x] Confirm clean CI builds for foundation and room topology revisions.
- [ ] Add reviewed, source-backed initial level records.

## World and transitions
- [x] Deterministic bounded room-topology generator (pure Java graph layer).
- [x] Chunk-to-room slice planning and boundary-specific doorway metadata.
- [x] Deterministic room-template selection with graph-derived doorway preservation.
- [x] Validated room material palettes and complete per-level room build planning.
- [x] Bounded interior decoration assets and chunk-local slicing.
- [ ] Authored structure assets and resource loading.
- [ ] Data-driven dimension and level bootstrap.
- [ ] Minecraft chunk-local room and decoration block realization.
- [ ] Transition placement from validated connection data.
- [ ] Safe multiplayer-aware level transfer.

## Gameplay
- [ ] Backrooms-specific survival resources and hazards.
- [ ] Lore-reviewed entities and server-authoritative AI.
- [ ] Sanity, fear, hallucination, memory, and paranoia systems.
- [ ] Persistent player and world discovery state.

## ARG and expansion
- [ ] Source-backed objects, groups, and phenomena.
- [ ] Documents, recordings, ciphers, and hidden structures.
- [ ] Multiplayer clue/discovery support.
- [ ] Expand level coverage while preserving categories.

## Release quality
- [ ] Dedicated-server integration tests.
- [ ] Multi-client multiplayer tests.
- [ ] World-generation and transition regression tests.
- [ ] Performance profiling and two full audits for each substantial update.
- [ ] Reproducible release JAR and versioned changelog.
