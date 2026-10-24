# JourneyFactions

A companion mod that displays faction territories on JourneyMap. Works on both client and server.

## Features

- **Real-time faction territory display** on JourneyMap
- **Hole detection** - territories with unclaimed chunks inside show correctly
- **Clean polygon rendering** - no ugly chunk grids
- **Multiple disconnected regions** support
- **Server-side support** - syncs territories directly with the standard Factions mod (no custom Factions fork required)

## Requirements

- **Minecraft 26.3**
- **Fabric Loader 0.20.0+**
- **Fabric API**
- **JourneyMap 6.0.0+** (Client)
- **Factions 2.9.6+** (Server)

## Installation

1. Download the latest release `.jar` file
2. Place the `.jar` file in your `mods` folder (on both client and server)
3. Launch Minecraft with Fabric

## Usage

### What You'll See
- **Faction territories** displayed as colored polygons on the map
- **Holes in territories** shown as unclaimed (gray) areas
- **Clean boundaries** without chunk grid lines
- **Multiple regions** for disconnected faction lands

## How It Works

JourneyFactions runs on both client and server:

1. **Server-side:** Hooks into the Factions mod and broadcasts territory claims and updates to connected players using custom Fabric networking payloads.
2. **Client-side:** Receives territory updates and visualizes them on JourneyMap using JourneyMap API v2 polygon overlays.

## Configuration

The mod works out of the box with no mandatory configuration needed. A config screen is available on the client when ModMenu and Cloth Config are installed.

## Building from Source

```bash
git clone https://github.com/Ultra119/JourneyFactions.git
cd JourneyFactions
./gradlew build
```

The compiled mod jar will be in `build/libs/`.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

- Original mod by [Arona74](https://github.com/Arona74/JourneyFactions)
- Ported to Minecraft 26.3 & JourneyMap API v2 by [Ultra119](https://github.com/Ultra119)
- Built with the [JourneyMap API](https://github.com/TeamJM/journeymap-api)
- Inspired by territory visualization needs in faction servers
