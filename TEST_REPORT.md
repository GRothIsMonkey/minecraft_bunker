# SITE-7 – Validation & Test Report

Plugin: `dist/BlacksiteBunker-1.0.0.jar` (sha256 `6450f2295b8fc780cc306b33338d43f164781ba8bad4e643c2c9c9d4441babb6`)

## 1. Test environment

| | |
|---|---|
| Server | **Paper 1.8.8** (git-PaperSpigot-445, "MC: 1.8.8", API 1.8.8-R0.1-SNAPSHOT) |
| Java | OpenJDK 21 (plugin compiled for Java 8 bytecode) |
| Machine | 4 vCPU Intel Xeon 2.1 GHz, `-Xmx3G` |
| World | default generated overworld, bunker at the default anchor -291 68 141 |
| Commands | typed in the server console without `/`, exactly as on EaglerHost |

## 2. Offline design analysis (every build of the plan)

The design is generated in memory (816,763 planned blocks). `Analyzer` then checks it against 1.8 rules
before anything touches a world:

| Check | Result |
|---|---|
| Rooms reachable on foot from the main gate (walk/jump/stairs/doors/ladders) | **125 / 125** |
| Floor spots a hostile mob could spawn on at y ≥ 40 with light < 8 | **0** |
| Slime-capable floor spots below y 40 | **0**, except 139 on the secret escape rail track (rails cannot be carpeted) |
| Iron doors without a working activator | **0** |
| Exposed redstone that players could break or trigger | **0** |
| Unsealed lava / fire hazards | **0** |
| Chests that 1.8 would merge into broken "triple chests" or re-orient | **0** |
| Attachables without valid 1.8 support (ladders, signs, buttons, levers, trapdoors, rails, pots, plates, doors, carpets, gravity blocks) | **0** |
| Finisher warnings | **0** |

## 3. Live tests on Paper 1.8.8

### Final run on the release JAR

The release JAR was run through all three scenarios back to back. Commands were typed in the console.

**A. Fresh world, default speed (25 ms/tick):** `bunker build` → `bunker confirm`

| | |
|---|---|
| Build time | **5 s**, with 0 "Can't keep up" warnings |
| Blocks | 627,820 placed, 188,943 already correct (natural air), 92 fixed by the repair pass, 0 changed again afterwards |
| Tile entities / entities | 514 / 63, 0 errors |
| Auto-verify | **PASS**: 816,763 blocks checked with 0 mismatched; 320 signs ok; 161 containers ok; 63/63 entities; 0 dark spots; beacon column clear; 14/14 lifts ok |

**B. Crash during the build:** `bunker speed 2`, build, then the server was killed with `SIGKILL` at 20% of the
structure phase. `state.yml` afterwards read `status: RUNNING, phase: STRUCTURE, cursor: 389499, clean-stop: false`.

**C. Restart and resume:**

- On startup the plugin warned: *"A SITE-7 build was interrupted in phase STRUCTURE. Run 'bunker resume' to
  continue safely. (The server was not shut down cleanly, so resume re-checks every phase from the start.)"*
- `bunker speed 25`, then `bunker resume`. The build finished and verify: **PASS**, with 0 mismatched blocks,
  63/63 entities and 0 dark spots.
- `bunker selftest`: **111 passed, 0 failed**.
  - Every iron door opens and closes when its button, lever or pressure plate is operated.
  - The armory lockers, the piston bookcase door and the lamp switches work (security lamps 2 → 0 lit,
    interrogation lamp 1 → 0).
  - The reactor SCRAM lights the alarm lamps (0 → 2).
  - All 14 lift signs work.
  - Super smelter: 6 furnaces working, 6 ingots delivered to the output chest.
  - Escape rail: up to the pump house in 32 s, back down to the station in 29 s, stopped at the bumper.
- `bunker verify` after the self-test: **PASS**. Every door, lever, lamp, chest and cart was restored.
- Server log: no plugin errors or exceptions.

### Earlier live runs

- Rebuilding over an existing SITE-7 is idempotent. About 20 blocks are re-placed (the daylight sensors, whose
  output changes with the time of day), 0 blocks need repair, 0 light sources are re-seated, and all entities are
  reused rather than duplicated. Verify: PASS.
- In an earlier run, a slow build (`bunker speed 3`) took 31 s with no "Can't keep up" warnings.
- The escape rail round trip was tested twice in a row on a fresh world: up in 32 s, down in 29 s, and the cart
  ended back at rest on the station brake.

## 4. Problems found by live testing, and fixed

Each of these was found on the real server, fixed, and re-tested:

| Found | Fix |
|---|---|
| After a **hard kill**, `resume` continued from the saved cursor, but chunks the server had not saved were lost. That left 22 of 64 entities (frames, paintings, stands) missing and 45 dark spots. | `state.yml` records whether the server shut down cleanly. After an unclean stop, `resume` re-walks every phase from the start, skipping blocks that are already correct. Re-tested: PASS. |
| Lighting left stale by the old resume path. | The lighting phase re-seats any light source whose neighbours are darker than they should be. |
| Replacing a hopper with a chest logged CraftBukkit "tile entity mismatch" errors. | The container is emptied and the cell goes through air first. No more errors. |
| Super smelter: only 1 of 12 furnaces got ore, because 1.8 hoppers pull from the hopper above them. | Redesigned: furnaces are fed in pairs from ore and fuel chests. The self-test smelts real iron ore: 6 furnaces working, ingots delivered. |
| Back-to-back storage rows formed 1.8 "triple chests", and the game re-oriented them. | An iron spine between the rows, the vault and archive rows split, and an analyzer rule added. |
| Lit furnaces went out, lamps went dark when placed before their power, paintings failed on occupied cells, and some console blocks were triggered by daylight sensors. | Furnaces plus glowstone, lamps powered before placement, cleared painting cells, and inert console bases. |
| Wall signs over openings, trapdoors hinged on other trapdoors, and a ladder backed by glowstone would pop off on the next block update. | Lintels, relocated signs, re-hinged trapdoors, and lights moved beside the ladder. An analyzer rule was added for all attachables. |
| Escape rail: the station cart sat beside the launcher, and the top bumper would have sent arriving carts straight back down. | Brake rails at both ends with launch buttons. The self-test rides the round trip. |

## 5. Known limitations

- **Slimes on the escape rail track:** 139 rail blocks below y 40 cannot carry carpet. In a slime chunk, a slime
  could appear on the track. They are harmless and rare, and the rest of the bunker is slime-proof.
- **Lift signs need the plugin.** The stairwell and ladders do not.
- **The first-pass repair is expected.** A fresh build reports about 92 blocks repaired. These are blocks the game
  changed while neighbouring cells were still natural stone: furnaces and chests auto-rotate on placement, and
  natural lava can flow in before the shell is sealed. The repair pass fixes them, and none reappear afterwards.
  The build report lists samples.
- On Java 9+ Paper 1.8.8 prints `java.lang.NoSuchFieldException: modifiers` during startup. It comes from the
  server itself, before plugins load, and is harmless.
