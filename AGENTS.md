# AGENTS.md

## Architecture

- **This branch (`1.21.1`):** Client-side Minecraft mod using Architectury for Fabric + NeoForge (1.21.1).
- **`1.20.1` branch:** Older version with Forge instead of NeoForge (1.20.1).
- **`velocity` branch:** Separate Gradle project — the Velocity proxy plugin. Different build system entirely.
- Three subprojects matching Architectury conventions: `common/` (shared logic), `fabric/`, `neoforge/`.
- Platform modules are thin entrypoints (~10 lines); all real code lives in `common/`.

## Build

- Java 21, Mojang official mappings.
- Gradle wrapper JAR is **missing** from git. Generate it first: `gradle wrapper` (if Gradle is installed) or restore from a cached copy.
- Commands: `./gradlew build`, `./gradlew :fabric:build`, `./gradlew :neoforge:build`.
- `gradle.properties` controls all versions and `enabled_platforms`.

## The core trick

- Mixin on `ClientIntentionPacket` constructor intercepts the `hostName` parameter via `@ModifyVariable` at `HEAD` and appends `$<target_server>`.
- The Velocity proxy plugin reads the modified VirtualHost, extracts the ID after `$`, and routes the player.
- `common/src/main/java/top/furryaxw/serverswitcher/mixin/ClientIntentionPacketMixin.java`

## Config (important quirk)

- Client config is a `.properties` file **baked into the JAR** at `assets/serverswitcher/switcher.properties`.
- Read-only at runtime — no save mechanism. Designed for modpack authors to pre-configure.
- Default target: `lobby`.

## Dead code

- `fabric/src/main/java/.../client/ServerswitcherFabricClient.java` — empty `onInitializeClient()`, NOT registered in `fabric.mod.json` entrypoints. Ignore it.

## No tests, no CI

- No test suites exist. No CI/CD pipelines. No linting or static analysis config.

## License

- MIT.
