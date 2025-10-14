package com.adventure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WorldBuilder {

    public GameWorld createWorld() {
        Map<String, Item> items = buildItems();
        Map<String, Room> rooms = buildRooms();
        Map<String, Npc> npcs = buildNpcs();

        linkRooms(rooms);
        seedItems(rooms);
        populateNpcs(rooms);

        Player player = new Player("forest_clearing");
        player.addItem("oak_compass");

        GameState gameState = new GameState();

        return new GameWorld(rooms, items, npcs, player, gameState);
    }

    private Map<String, Item> buildItems() {
        Map<String, Item> items = new HashMap<>();
        items.put("oak_compass", new Item(
            "oak_compass",
            "Oak Compass",
            "A palm-sized disc of polished oak etched with faint runes. It hums softly when you face true north.",
            true
        ));
        items.put("sage_note", new Item(
            "sage_note",
            "Folded Note",
            "Ink strokes in a hurried hand: \"Seek Meryl beyond the village walls. She keeps the old ways.\"",
            true
        ));
        items.put("silver_coin", new Item(
            "silver_coin",
            "Silver Coin",
            "A tarnished coin stamped with the emblem of the Verdant Vale. One good trade left in it.",
            true
        ));
        items.put("lantern", new Item(
            "lantern",
            "Traveler's Lantern",
            "An iron lantern with a hinged glass pane. The wick smells faintly of pine pitch.",
            true
        ));
        items.put("glowcap", new Item(
            "glowcap",
            "Glowcap Mushroom",
            "A mushroom whose gills glow with a gentle blue light. The sage will know its worth.",
            true
        ));
        items.put("rune_key", new Item(
            "rune_key",
            "Rune-Key",
            "A weighty key of interlocking stone prisms. It thrums in your palm, eager to find its lock.",
            true
        ));
        items.put("heartseed", new Item(
            "heartseed",
            "Heartseed",
            "A crystalline seed pulsing with warm emerald light. Life itself seems to breathe within.",
            true
        ));
        items.put("river_shell", new Item(
            "river_shell",
            "River Shell",
            "A spiral shell lined with silvered nacre. It whispers with the echo of distant waves.",
            true
        ));
        return items;
    }

    private Map<String, Room> buildRooms() {
        Map<String, Room> rooms = new HashMap<>();

        rooms.put("forest_clearing", new Room(
            "forest_clearing",
            "Forest Clearing",
            "Moonlight filters through towering pines, painting the clearing in silvery patterns. A ring of standing stones hums with quiet power, and a faint trail leads outward.",
            "The clearing feels expectant, the standing stones warm beneath your fingertips.",
            false
        ));

        rooms.put("misty_path", new Room(
            "misty_path",
            "Misty Path",
            "A narrow path winds through ferns. Mist curls around your boots while fireflies bob like lanterns hung by unseen hands.",
            "The path is quieter now, the mist parting as though it remembers you.",
            false
        ));

        rooms.put("ancient_oak", new Room(
            "ancient_oak",
            "Ancient Oak",
            "An oak older than legend anchors the grove, its branches hung with bells of bone and ivy. A hollow in the roots breathes out a gentle warmth.",
            "The oak's branches sway in a wind that does not touch the ground.",
            false
        ));

        rooms.put("shimmering_brook", new Room(
            "shimmering_brook",
            "Shimmering Brook",
            "Water chatters over smooth stones, each one sparkling as if dusted with stars. Something glints beneath the current.",
            "The brook sings the same bright song—yet new ripples race ahead of your steps.",
            false
        ));

        rooms.put("cave_mouth", new Room(
            "cave_mouth",
            "Cave Mouth",
            "A yawning cave splits the hillside, breathing out damp, cold air. Scratches on the stone show where claws once dragged something heavy inside.",
            "The cave waits, patient and dark. A faint gust smells of mineral and moss.",
            false
        ));

        rooms.put("twisting_cavern", new Room(
            "twisting_cavern",
            "Twisting Cavern",
            "The cavern tunnels twist like a serpent's spine. Stalactites drip luminous beads that vanish before they hit the ground.",
            "You remember the turns now, the walls gleaming with faint phosphorescence.",
            true
        ));

        rooms.put("underground_lake", new Room(
            "underground_lake",
            "Underground Lake",
            "An underground lake spreads out, black as ink. At its center, mushrooms glow with a sapphire radiance, their light reflecting in ripples.",
            "The glowcaps bow as if in greeting, their light steady as heartbeat.",
            true
        ));

        rooms.put("village_gate", new Room(
            "village_gate",
            "Village Gate",
            "Tall wooden gates stand open, a carved relief of a dragon curling across them. Lanterns sway gently, tended by unseen keepers.",
            "The gate creaks softly, the lanterns dipping low like nodding sentinels.",
            false
        ));

        rooms.put("village_square", new Room(
            "village_square",
            "Village Square",
            "Cobblestones radiate from an ancient well. Merchants' stalls stand quiet but ready, their awnings stitched with protective runes.",
            "Villagers watch you with curious hope as you step across the square.",
            false
        ));

        rooms.put("tavern_common", new Room(
            "tavern_common",
            "Bran's Tavern",
            "The tavern glows with hearth light. The air smells of spiced cider, and a wall of mismatched lanterns flickers in rhythm.",
            "Bran polishes a mug as though expecting news from you.",
            false
        ));

        rooms.put("sage_hut", new Room(
            "sage_hut",
            "Sage's Hut",
            "Bundles of herbs dangle from the rafters. Scrolls unfurl across the table, each weighted with crystals that hum softly.",
            "Meryl's eyes follow you with sharp kindness, ink staining her fingertips.",
            false
        ));

        rooms.put("market_lane", new Room(
            "market_lane",
            "Market Lane",
            "Shuttered stalls line the lane, their wares hidden beneath woven cloth. A stray cat regards you from atop a crate.",
            "The scent of dried fruit and warm bread lingers despite the empty stalls.",
            false
        ));

        rooms.put("ruined_tower", new Room(
            "ruined_tower",
            "Ruined Tower",
            "A collapsed tower leans against the sky. A stone door marked with spiraling runes blocks the way inside.",
            "The tower thrums with contained power, the runes almost warm beneath your hand.",
            false
        ));

        rooms.put("tower_entry", new Room(
            "tower_entry",
            "Tower Stair",
            "Inside the tower, a spiral stair climbs past arrow slits that show only swirling storm clouds.",
            "The stair remembers your tread, the storm beyond whispering secrets.",
            false
        ));

        rooms.put("tower_summit", new Room(
            "tower_summit",
            "Tower Summit",
            "At the tower's peak, a dais of living root cradles a crystal seed. A wisp-like guardian circles it, trailing sparks.",
            "The guardian drifts aside as you approach, embers settling into your shoulders like trust.",
            false
        ));

        rooms.put("riverbank", new Room(
            "riverbank",
            "Riverbank",
            "A broad river curls around smooth stones where a fisherman tends silent nets. Fireflies draw constellations over the water.",
            "Len the fisherman nods as though you both share the same dream.",
            false
        ));

        return rooms;
    }

    private Map<String, Npc> buildNpcs() {
        Map<String, Npc> npcs = new HashMap<>();
        npcs.put("bran", new Npc("bran", "Innkeeper Bran", "A broad-shouldered man with a ready laugh and shrewd eyes."));
        npcs.put("meryl", new Npc("meryl", "Sage Meryl", "A silver-haired sage whose gaze feels like moonlight on water."));
        npcs.put("len", new Npc("len", "Fisher Len", "A quiet fisher whose nets drip with riverlight."));
        npcs.put("wisp", new Npc("wisp", "Guardian Wisp", "A swirling mote of light that hums with ancient devotion."));
        return npcs;
    }

    private void linkRooms(Map<String, Room> rooms) {
        rooms.get("forest_clearing").addExit("north", "misty_path");
        rooms.get("forest_clearing").addExit("east", "shimmering_brook");
        rooms.get("forest_clearing").addExit("west", "village_gate");

        rooms.get("misty_path").addExit("south", "forest_clearing");
        rooms.get("misty_path").addExit("east", "ancient_oak");
        rooms.get("misty_path").addExit("north", "ruined_tower");

        rooms.get("ancient_oak").addExit("west", "misty_path");

        rooms.get("shimmering_brook").addExit("west", "forest_clearing");
        rooms.get("shimmering_brook").addExit("east", "cave_mouth");
        rooms.get("shimmering_brook").addExit("south", "riverbank");

        rooms.get("riverbank").addExit("north", "shimmering_brook");

        rooms.get("cave_mouth").addExit("west", "shimmering_brook");
        rooms.get("cave_mouth").addExit("in", "twisting_cavern");

        rooms.get("twisting_cavern").addExit("out", "cave_mouth");
        rooms.get("twisting_cavern").addExit("east", "underground_lake");

        rooms.get("underground_lake").addExit("west", "twisting_cavern");

        rooms.get("village_gate").addExit("east", "forest_clearing");
        rooms.get("village_gate").addExit("west", "village_square");

        rooms.get("village_square").addExit("east", "village_gate");
        rooms.get("village_square").addExit("north", "market_lane");
        rooms.get("village_square").addExit("west", "tavern_common");
        rooms.get("village_square").addExit("south", "sage_hut");

        rooms.get("tavern_common").addExit("east", "village_square");

        rooms.get("sage_hut").addExit("north", "village_square");

        rooms.get("market_lane").addExit("south", "village_square");

        rooms.get("ruined_tower").addExit("south", "misty_path");
        rooms.get("ruined_tower").addExit("in", "tower_entry");

        rooms.get("tower_entry").addExit("out", "ruined_tower");
        rooms.get("tower_entry").addExit("up", "tower_summit");

        rooms.get("tower_summit").addExit("down", "tower_entry");
    }

    private void seedItems(Map<String, Room> rooms) {
        rooms.get("forest_clearing").addItem("sage_note");
        rooms.get("shimmering_brook").addHiddenItem("silver_coin");
        rooms.get("underground_lake").addItem("glowcap");
        rooms.get("tower_summit").addItem("heartseed");
    }

    private void populateNpcs(Map<String, Room> rooms) {
        rooms.get("tavern_common").setNpcs(List.of("bran"));
        rooms.get("sage_hut").setNpcs(List.of("meryl"));
        rooms.get("riverbank").setNpcs(List.of("len"));
        rooms.get("tower_summit").setNpcs(List.of("wisp"));
    }
}
