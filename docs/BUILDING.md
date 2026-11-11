# Building The Minerooms

## Requirements

- JDK 21 (Gradle daemon must also run on Java 21).
- Gradle 8.12.1.
- Network access on first build for Gradle and NeoForge dependencies.

## Commands

```bash
gradle --version
gradle --no-daemon clean build
```

The mod JAR is written to `build/libs/`; unit tests run as part of `build`.

Launch development environments with `gradle runClient` or `gradle runServer`. The server run may create a local EULA file; review and accept Minecraft's EULA yourself before running a server.

## Codespaces

Open the repository, verify `java -version` reports Java 21, then run `gradle --no-daemon build`. CI installs Java 21 and Gradle 8.12.1 on GitHub-hosted runners.

## Troubleshooting

- Wrong Java: set JAVA_HOME to JDK 21 and restart the terminal/Gradle daemon.
- Dependency resolution: check access to Gradle Plugin Portal and NeoForge Maven.
- Out of memory: reduce Gradle workers or increase available Codespaces memory; do not assign more heap than the environment can provide.
- Client-only crash on server: move client references into client-only setup; keep common initialization side-neutral.
