# SITE-7 Blacksite Bunker – Install & Build Guide

**File to upload:** [`dist/BlacksiteBunker-1.0.0.jar`](dist/BlacksiteBunker-1.0.0.jar)
**Server:** Spigot / Paper / CraftBukkit **1.8.8** (this is what EaglerHost runs behind Eaglercraft 1.8.8)
**Main entrance:** `X=-291 Y=68 Z=141`, blast door **facing EAST**
**Area changed:** `x -436..-222, y 4..127, z 79..188`

The plugin builds the whole bunker for you in small batches, so you never paste a long command into the
web console. The only commands you need are short ones like `bunker build`.

---

## 1. Upload the plugin (EaglerHost or any 1.8.8 host)

1. **Back up your world first.** The plugin only changes the planned area above, but a backup lets you undo
   everything. On EaglerHost use the panel's backup feature, or download the `world` folder with the file manager.
2. Open the panel's **File Manager** and go to the server's **`plugins`** folder.
   - If your host runs a **proxy** (EaglerXBungee / BungeeCord) *and* a game server, upload the plugin to the
     **game server** (the Spigot/Paper 1.8.8 one that has the `world` folder), **not** the proxy.
3. Upload **`BlacksiteBunker-1.0.0.jar`**.
4. **Restart** the server. A `reload` is not recommended.
5. The console should now show:
   ```
   [BlacksiteBunker] SITE-7 blacksite builder ready. Entrance anchor -291 68 141, facing EAST. Use 'bunker build' then 'bunker confirm'.
   ```
   A new folder `plugins/BlacksiteBunker/` holds `config.yml`, `state.yml` (build progress) and `reports/`.

## 2. Build it

In the **web console**, type the commands **without** a `/`. In game, as an op, type them **with** `/`.

| Step | Console command | What happens |
|---|---|---|
| 1 | `bunker build` | Nothing is built yet. Shows a warning with the exact area, the entrance and its facing, and asks you to confirm. |
| 2 | `bunker confirm` | Must be typed **within 60 seconds** of step 1. Construction starts. |
| 3 | `bunker status` | Progress %, current phase and ETA, any time you like. |

The build runs in the background for a fixed number of milliseconds per server tick (25 ms by default), so the
server stays playable. Progress messages appear every 10%. When it finishes, the plugin checks the result
automatically and prints `RESULT: PASS`.

Typical build time: **about 5 seconds to a few minutes**. It was 5 seconds on the test machine; slower shared
hosts take longer. Everything is loaded and kept loaded by the plugin, so no player has to be nearby.

When it says **Build complete**, go and see it:

- In game: `/bunker tp entrance` (or walk to `-291 68 141` and approach from the east).
- Other quick jumps: `/bunker tp l1` … `/bunker tp l6`, `/bunker tp command`, `/bunker tp reactor`,
  `/bunker tp hangar`, `/bunker tp storage`.

## 3. If something interrupts the build

| Situation | What to do |
|---|---|
| You want to stop for a while | `bunker pause`, later `bunker resume` |
| Server restarted or stopped normally | Start the server, then `bunker resume`. It carries on where it stopped. |
| Server **crashed**, was killed or ran out of memory | Start the server, then `bunker resume`. The plugin sees that the last shutdown was not clean. It re-checks every phase from the start, because chunks the server had not saved are lost. Blocks already in place are skipped quickly. |
| You changed your mind before confirming | `bunker cancel` (or just wait 60 seconds) |
| Anything looks damaged later | `bunker build` + `bunker confirm` again. It only fixes what differs and resets chests to their original contents. |

Progress is stored in `plugins/BlacksiteBunker/state.yml`, so it survives restarts.

## 4. Check it

- `bunker verify` checks every planned block (816,763), every sign, chest, armor stand, frame and painting, the
  light level on every floor block, the beacon beam and all 14 lift signs. It prints `RESULT: PASS` or lists
  problems. The full report is in `plugins/BlacksiteBunker/reports/verify-latest.txt`.
- `bunker selftest` operates the facility for you: every iron door with its buttons, levers and pressure plates,
  the piston bookcase, the light switches, the reactor SCRAM, every lift, the super smelter (with real
  items) and a round trip on the secret escape rail. Everything is put back afterwards. It takes about 2
  minutes. Expected: `111 passed, 0 failed`.

## 5. All commands

Aliases: `bunker` = `blacksite` = `bsb` = `site7`.

| Command | Permission | Purpose |
|---|---|---|
| `bunker` / `bunker help` | admin | Command list and current state |
| `bunker info` | admin | Entrance, facing, bounds, footprint, depth, levels, room count |
| `bunker build` | admin | Show the warning and bounds, then wait for confirmation (changes nothing) |
| `bunker confirm` | admin | Start building (within 60 s of `build`) |
| `bunker cancel` | admin | Cancel a pending build, or pause a running one |
| `bunker status` | admin | Phase, progress, ETA, counters |
| `bunker pause` / `bunker resume` | admin | Pause or continue (also after a restart or crash) |
| `bunker verify` | admin | Full check with report file |
| `bunker selftest [name]` | admin | Operate doors, levers, piston door, lights, lifts, smelter (optionally only tests whose name contains `name`) |
| `bunker speed <2-45>` | admin | Milliseconds per tick the builder may use (default 25) |
| `bunker report` | admin | Where the last reports are |
| `bunker probe x y z [sx sy sz]` | admin | Diagnostic: prints block ids at a spot |
| `bunker tp <entrance\|l1..l6\|command\|reactor\|hangar\|storage>` | `blacksitebunker.tp` (op) | Teleport inside the bunker |

Permissions: `blacksitebunker.admin` (default: op), `blacksitebunker.tp` (default: op),
`blacksitebunker.lift` (default: everyone, uses the lift signs).

## 6. Keep the plugin installed after building

Everything in the bunker is ordinary 1.8 blocks, so the bunker stays even if you remove the plugin. The
**`[Lift Up]` / `[Lift Down]` signs** are the only part that needs the plugin. They are the personnel lift on
every level and the freight lift from Loading Bay B to the hangar. Without the plugin, use the main stairwell
and ladders, which reach every level.

## 7. Settings (`plugins/BlacksiteBunker/config.yml`)

| Setting | Default | Meaning |
|---|---|---|
| `anchor.x/y/z` | `-291 / 68 / 141` | Where the main blast door is. The whole complex moves with it. Do not change it between `build` and the end of the build. |
| `world` | `""` | World to build in (empty = the main world) |
| `tick-budget-ms` | `25` | Builder time per tick. Lower on a busy server, raise on an empty one. |
| `confirm-seconds` | `60` | Time allowed for `bunker confirm` |
| `furnish-loot` | `true` | Themed supplies in chests. `false` = empty chests, lore books only. |
| `auto-verify` | `true` | Verify automatically after building |
| `lifts-enabled` | `true` | Sign lifts on/off |
| `lifts-inside-bunker-only` | `true` | Lift signs only work inside the bunker |
| `remove-spawners-in-bounds` | `true` | Removes natural dungeon spawners that end up inside the area |

Restart after editing the file, or use `bunker speed` for the budget.

## 8. Performance tips

- Default 25 ms/tick keeps 20 TPS on a normal host. If players notice lag, use `bunker speed 10` while it
  builds. If nobody is online, `bunker speed 40` finishes fastest.
- Building needs little memory: the plan takes a few tens of MB while it is used. Any host that runs a
  1.8.8 server with 1 GB RAM or more is fine.
- The builder keeps the site's chunks (about 120) loaded only while it runs, then lets them unload normally.
- Dropped items and stray mobs inside the area are cleaned up at the end. Nothing is left lying around.

## 9. Troubleshooting

| Message / problem | Fix |
|---|---|
| `Unknown command` | The JAR is not in the game server's `plugins` folder, or the server was not restarted. Check the startup log for `[BlacksiteBunker]`. |
| `Type bunker confirm within 60 seconds` but confirm says nothing is pending | Wait longer than 60 s and the request expires. Run `bunker build` again. |
| `config.yml anchor/loot changed since the build started` on resume | Put the old values back, or run `bunker build` again with the new ones. |
| `RESULT: ATTENTION NEEDED` | Read `reports/verify-latest.txt`. Running `bunker build` + `bunker confirm` again repairs blocks, chests, frames and lighting. |
| Lift sign does nothing | `lifts-enabled` must be `true`, and the player needs `blacksitebunker.lift`. |
| Console shows `NoSuchFieldException: modifiers` at startup | Harmless Paper 1.8.8 warning on newer Java versions. It is not from this plugin. |

## 10. Build from source (optional)

```
mvn -B package        # needs JDK 8+ and Maven; output: target/BlacksiteBunker-1.0.0.jar
```
