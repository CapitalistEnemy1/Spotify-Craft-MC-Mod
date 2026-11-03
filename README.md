# SpotifyCraft 

**I have spoken to my lawyer he told me if I want to continue this we can but if we are asked to take it down we shall oblige**

**if we are asked to take it down we will just rebuild the mod from scratch and rebrand and rebuild without their source code**

**Live Spotify lyrics inside Minecraft.**

Spotify Craft brings your currently playing music into Minecraft with animated, 3D lyrics that appear in the sky and stay in sync with your song. Enjoy a now-playing display, album artwork, visualisers, custom skies, cosmetics, and a glass-style hotbar.

The current source for this github is only for 1 jar (1.21.11), so when you build it it will only be compatible with 1.21.11!

> **Free and open source:**  

## Contents

- [Features](#features)
- [Compatibility](#compatibility)
- [Downloads](#downloads)
- [Installation](#installation)
- [Default Keybinds](#default-keybinds)
- [Lyrics and Spotify](#lyrics-and-spotify)
- [Security and Virus Scanning](#security-and-virus-scanning)
- [Source Code and Licence](#source-code-and-licence)
- [Disclaimer](#disclaimer)

## Features

- **Live 3D lyrics** — lyrics float in the Minecraft sky and follow your current song.
- **Now-playing display** — see the song title, artist, album artwork, and a beat-reactive visualiser.
- **15 custom skies** — choose from themes such as Aurora, Nebula, Synthwave, Black Hole, Cosmos, and more.
- **Glass-style hotbar** — customise your HUD with a translucent look and a custom crosshair.
- **Cosmetics** — equip accessories including neon headphones, a conical hat, a crown, a halo, a pumpkin head, a mini Saturn, and more.
- **In-game settings menu** — press `J` to adjust lyric size, opacity, glow, font, animation, timing offset, accent colour, performance options, and more.
- **Saved lyrics** — bookmark lines you want to keep.
- **Chill and PvP profiles** — quickly switch between a full visual experience and a lighter setup.
- **Spotify Free and Premium support** — connect your Spotify account. Playback controls such as skipping and pausing require Premium.
- **Client-side mod** — no installation on the Minecraft server is required.

## Compatibility

Choose the download that matches your Minecraft version.

| Minecraft version | Download | Required Java |
| --- | --- | --- |
| 1.21.11 | [Spotify_Craft_1.21.11_fixed.jar](https://github.com/CapitalistEnemy1/Spotify-Craft-/releases/download/4.0.0/Spotify_Craft_1.21.11.jar) | Java 21 or newer |
| 26.1 | [Spotify_Craft_26.1_fixed.jar](https://github.com/CapitalistEnemy1/Spotify-Craft-/releases/download/4.0.0/Spotify_Craft_26.1.jar) | Java 25 or newer |
| 26.1.1 | [Spotify_Craft_26.1.1_fixed.jar](https://github.com/CapitalistEnemy1/Spotify-Craft-/releases/download/4.0.0/Spotify_Craft_26.1.1.jar) | Java 25 or newer |
| 26.1.2 | [Spotify_Craft_26.1.2_fixed.jar](https://github.com/CapitalistEnemy1/Spotify-Craft-/releases/download/4.0.0/Spotify_Craft_26.1.2.jar) | Java 25 or newer |
| 26.2 | [Spotify_Craft_26.2_fixed.jar](https://github.com/CapitalistEnemy1/Spotify-Craft-/releases/download/4.0.0/Spotify_Craft_26.2.jar) | Java 25 or newer |
| 26.3 | [Spotify_Craft_26.3_fixed.jar](https://github.com/CapitalistEnemy1/Spotify-Craft-/releases/download/4.0.0/Spotify_Craft_26.3.jar) | Java 25 or newer |

**[Browse all releases](https://github.com/CapitalistEnemy1/Spotify-Craft-/releases/tag/4.0.0)**

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) version **0.18.0 or newer** for your Minecraft version.
2. Download the matching [Fabric API](https://modrinth.com/mod/fabric-api) version.
3. Place the Fabric API `.jar` file in your Minecraft `mods` folder.
4. Download the Spotify Craft `.jar` for your Minecraft version from the [releases page](https://github.com/CapitalistEnemy1/Spotify-Craft-/releases/tag/1.0.0).
5. Place the Spotify Craft `.jar` in the same `mods` folder.
6. Launch Minecraft and open Spotify.
7. Start playing a song, then press **`J`** in Minecraft to open the mod menu and configure your experience.

Make sure your Minecraft version, Fabric Loader, Fabric API, and Java version are compatible.

## Default Keybinds

| Key | Action |
| --- | --- |
| `J` | Open the Spotify Craft menu |
| `F9` | Play the next song |
| `N` | Save the current lyrics line |
| `F8` | Switch between Chill and PvP profiles |

You can rebind these keys, along with controls for pausing, playing the previous song, toggling sky lyrics, and syncing lyrics at your location, under **Options → Controls → Lyrics Cord**.

## Lyrics and Spotify

- Lyrics are fetched from [LRCLIB](https://lrclib.net/).
- Your Spotify login stays on your PC, according to the project's stated design.
- Spotify Free and Premium accounts are supported; some playback controls require Premium.
- Spotify Craft is a client-side mod, so it does not need to be installed on a multiplayer server.

## Virus Check and Security

The project includes screenshots and links to help you inspect the repository and its files yourself. Please do your own checks before running any downloaded software.

### Virus-scan screenshots

![Virus scan screenshot](https://github.com/CapitalistEnemy1/Spotify-Craft-/blob/main/non-malware-proof.png?raw=true)

![Batch-file scan screenshot](https://github.com/CapitalistEnemy1/Spotify-Craft-/blob/main/no-malware-bat2.png?raw=true)

![Additional virus-scan screenshot](https://github.com/CapitalistEnemy1/Spotify-Craft-MC-Mod/blob/main/no-malware.png?raw=true)

### Check it yourself

- **For the whole repository:** [ScanRepo — Spotify-Craft-MC-Mod](https://www.scanrepo.dev/scan/github/CapitalistEnemy1/Spotify-Craft-MC-Mod)
- **For individual files:** [Upload a file to VirusTotal](https://www.virustotal.com/gui/home/upload)
- **Inspect the build workflow:** [GitHub Actions — build.yml](https://github.com/CapitalistEnemy1/Spotify-Craft-MC-Mod/actions/workflows/build.yml)
- **Review the source code:** [Spotify-Craft-MC-Mod on GitHub](https://github.com/CapitalistEnemy1/Spotify-Craft-MC-Mod)

### Why does the batch file show an 8% detection score?

The explanation provided by the project maintainer is that the batch file is used to build the mod from source after the repository is updated, and is not included in the mod itself. You can inspect the [build workflow](https://github.com/CapitalistEnemy1/Spotify-Craft-MC-Mod/actions/workflows/build.yml) and submit the batch file to VirusTotal or another scanner to review the results yourself.

**Important:** A detection percentage alone cannot prove that a file is malicious or harmless. Antivirus tools can produce false positives, and a clean scan does not guarantee that software is safe. Review the source code and scan results yourself, and only run files you trust.

Remember: check files before running them and stay safe online — click clever, click safe.

## Source Code and Licence

This project states that it is distributed under the **MIT Licence**. See the repository's [`LICENSE`](LICENSE) file for the applicable terms.

The MIT Licence generally permits use, modification, and redistribution, subject to its conditions, including preservation of the copyright and permission notice. Make sure the licence file and notices required by the licence are included when redistributing the project.

- [Main project repository](https://github.com/CapitalistEnemy1/Spotify-Craft-)
- [Source/build repository](https://github.com/CapitalistEnemy1/Spotify-Craft-MC-Mod)
- [MIT Licence details](https://opensource.org/license/mit)

## Disclaimer

Spotify Craft is an unofficial fan project and is not affiliated with, endorsed by, or sponsored by Spotify AB or Mojang/Microsoft. Spotify and Minecraft are trademarks of their respective owners.

Lyrics are provided by [LRCLIB](https://lrclib.net/). Use of Spotify services remains subject to Spotify's applicable terms.

---

**Enjoy your music in Minecraft!**
