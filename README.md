# AntiCheat Test Suite

A client-side Fabric mod for stress-testing your own Minecraft server's
anticheat. Every module reproduces the real client behaviour a cheat client
would produce — real movement packets, real interaction packets — rather
than faking anything locally, so your anticheat gets genuine signal.

**Only run this against a server you own or have explicit permission to
test on.** On any other server this is indistinguishable from actually
cheating and will get the account banned like any other client would.

## Two versions, two folders

Minecraft moved to year-based versioning in 2026. `1.21.11` (Dec 2025,
"Mounts of Mayhem") was the last release using the old `1.x.y` scheme and
the last obfuscated one; `26.2` (June 2026, "Chaos Cubed") is current and
ships unobfuscated. They need different toolchains, so this is two
self-contained mod projects with identical logic:

- `anticheat-test-1.21.11/` — targets Java Edition 1.21.11, built against
  Yarn mappings. This one I wrote with high confidence — it's within my
  training data and uses well-established, stable Fabric API calls.
- `anticheat-test-26.2/` — targets Java Edition 26.2, built against
  official Mojang mappings (Yarn doesn't exist for this version). **26.2
  released after my knowledge cutoff**, so treat this as a solid
  best-effort port rather than verified-correct: I've flagged every line
  I'm not fully certain about with a `// VERIFY:` comment. Expect to fix a
  handful of method names via your IDE's autocomplete/"Generate Sources"
  before it compiles — that's normal for porting any mod across a mapping
  change, not a sign something is fundamentally wrong.

You only need to build the one matching whichever version your test server
actually runs.

## Building

Each folder is a standalone Gradle project:

```bash
cd anticheat-test-1.21.11   # or anticheat-test-26.2
./gradlew build
```

The output jar lands in `build/libs/`. Drop it in your test client's `mods/`
folder alongside a matching [Fabric API](https://modrinth.com/mod/fabric-api)
build and [Fabric Loader](https://fabricmc.net/use/).

Before building, check the version numbers pinned in `gradle.properties`
against the current recommendations at https://fabricmc.net/develop/ —
Fabric bumps loader/API patch releases often and a stale one can fail to
resolve.

## Modules (default keybinds, rebindable in Options > Controls)

| Key | Module | What it tests |
|---|---|---|
| Numpad 1 | **Fly** | Overrides vertical velocity directly each tick (not the creative "flying" flag — a naive check that only reads that flag won't catch this, same as it wouldn't catch a real survival-mode fly hack). Hold Jump to ascend, Sneak to descend. |
| Numpad 2 | **Speed** | Scales horizontal velocity by a configurable multiplier (default 2.5x) on top of normal input. |
| Numpad 3 | **NoSlowdown** | Ignores cobweb/soul sand friction — one of the oldest, highest-confidence anticheat checks. |
| Numpad 4 | **AutoClutch** | Watches fall speed; once falling fast enough to take damage, auto-places a water bucket underneath and scoops it back up a few ticks later. Good target for timing/pattern-based clutch detection, since a real player *can* clutch manually. |
| Numpad 5 | **Combat** | Extended reach (attacks beyond the vanilla ~3 block limit) and auto-attack ("killaura") on the nearest living entity, independent toggles. |

Tuning knobs (fall threshold, speed multiplier, reach distance, etc.) are
public fields on each module class in `module/ModuleManager.java` — no
config file, just edit and rebuild for now.

A small HUD in the top-right corner lists which modules are currently
active, purely so you can see the mod's own state while testing — that HUD
is client-only and has no bearing on what your server actually sees.

## A note on scope

I couldn't compile-test either build myself — this environment doesn't
have network access to Minecraft's Maven repositories (`maven.fabricmc.net`,
`libraries.minecraft.net`, etc.), so I can't run Gradle against the real
game jar. The 1.21.11 build should compile clean; the 26.2 build is a
strong starting point that may need the small fixes noted above.
