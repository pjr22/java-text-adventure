# Verdant Vale Text Adventure

Verdant Vale is a story-rich, late-70s style text adventure written in Java. You awaken beneath moonlit pines with only an oak compass for guidance and a blighted wilderness in need of restoration. Travel through forests, caverns, villages, and the ruined tower, gather items, solve puzzles, and befriend the vale’s denizens to return the Heartseed to the Ancient Oak.

## Build

The project is pure Java (no external dependencies).

```bash
javac -d out src/main/java/com/adventure/*.java
```

The command above compiles all sources into the `out/` directory. Run it again whenever you change the code.

## Run

Launch the game from the project root after compiling:

```bash
java -cp out com.adventure.Main
```

Optional: append `load` to auto-load the default save on startup.

Saves are stored under `saves/` (created automatically); use multiple slots with `save <name>` and `load <name>` in-game.

## Basic Commands

- `look` / `examine <thing>` – See your surroundings or inspect items and characters.
- `go <direction>` – Travel (`north`, `south`, `east`, `west`, `up`, `down`, `in`, `out`). Short forms (`n`, `e`, etc.) are supported.
- `take <item>` / `drop <item>` – Manage your inventory.
- `inventory` (or `i`) – Review what you carry.
- `talk <name>` – Converse with nearby NPCs for hints and items.
- `use <item>` – Activate something you hold (light the lantern, unlock the tower, plant the Heartseed, listen to the shell…).
- `search` – Sweep the area for hidden objects.
- `save [slot]` / `load [slot]` – Preserve or restore progress; omit the slot to use the default file.
- `quit` – Exit, with a prompt to save.

Type `help` in-game to see the full command list at any time.

## Tips for Adventurers

- **Listen to characters.** Bran, Meryl, Len, and the wisp all give clues about the next steps or items you need.
- **Bring light underground.** The caverns and underground lake demand a lit lantern before you can safely explore.
- **Search sparkling places.** Areas with hints of hidden surprises often reveal crucial items (`search`).
- **Follow the oak’s guidance.** Key story beats circle back to the Ancient Oak—remember to return once you recover the Heartseed.
- **Save before big experiments.** Slot-based saves let you try bold ideas without losing progress.

Restore the Verdant Vale and let its bells ring once more!

