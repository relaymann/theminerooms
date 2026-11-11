# The Minerooms

A lore-focused Backrooms exploration and horror mod for Minecraft Java Edition 1.21.1, built on NeoForge 21.1.262 and Java 21.

The project follows the **Backrooms Fandom Wiki** as its selected continuity. The mod combines liminal exploration, survival horror, psychological horror, enhanced lore-faithful entity behavior, and community-oriented ARG discoveries.

> **Project status:** Foundation in progress. No level connections or entity behaviors are considered canon in-game until they have been checked against and documented from the selected Fandom Wiki source.

## Design pillars

- Open exploration; no mandatory linear level sequence.
- Hybrid generation: authored landmark layouts and structures combined with procedural room/ corridor composition.
- Lore-backed levels, entities, objects, groups, phenomena, entrances, and exits.
- Each released level has **one or two documented entrances and one or two documented exits**. This is a deliberate gameplay scope limit, not a claim that the wiki documents only that many.
- Every implemented connection records its source, conditions, discovery method, destination, and rarity where applicable.
- Psychological systems: sanity, fear, hallucinations, memory, and paranoia.
- Backrooms-specific survival systems.
- Dedicated-server-safe multiplayer as a first-class requirement.
- ARG clues that reward investigation without making ordinary progression depend on external websites.

## Technical baseline

| Component | Target |
|---|---|
| Minecraft | Java Edition 1.21.1 |
| Mod loader | NeoForge 21.1.262 |
| Java | JDK 21 |
| Language | Java |
| Build | Gradle + NeoForge ModDevGradle |
| Distribution | Standalone mod JAR |
| Multiplayer | Integrated and dedicated server |

## Build

Use JDK 21 and Gradle 8.12.1 or a compatible Gradle 8.x release supported by the configured ModDevGradle plugin.

```bash
gradle build
```

The distributable JAR is produced under `build/libs/`.

For client development:

```bash
gradle runClient
```

For a dedicated-server development run:

```bash
gradle runServer
```

See [docs/BUILDING.md](docs/BUILDING.md) for setup and troubleshooting.

## Documentation

- [Architecture](docs/ARCHITECTURE.md)
- [Lore and canon policy](docs/LORE_POLICY.md)
- [Level and connection data format](docs/LEVEL_DATA.md)
- [Development roadmap](docs/ROADMAP.md)
- [Build instructions](docs/BUILDING.md)

## Lore sourcing

The selected reference continuity is the [Backrooms Wiki on Fandom](https://backrooms.fandom.com/wiki/Backrooms_Wiki). Individual level, entity, object, and group pages must be cited in project data and documentation. Do not infer a connection from a general overview when the destination page does not support it. Keep uncertain, disputed, deleted, archived, and joke material out of released content unless explicitly approved as a separate category.

## License

No license has been selected yet. Until one is added, all rights are reserved by default.