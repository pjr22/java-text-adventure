package com.adventure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public final class Game {
    private static final String FLAG_LANTERN_LIT = "lanternLit";
    private static final String FLAG_LANTERN_GIVEN = "branLanternGiven";
    private static final String FLAG_MERYL_GAVE_KEY = "merylGaveKey";
    private static final String FLAG_TOWER_UNLOCKED = "towerUnlocked";
    private static final String FLAG_LEN_GAVE_SHELL = "lenGaveShell";
    private static final String FLAG_WISP_BLESSED = "wispBlessed";

    private final Scanner scanner = new Scanner(System.in);
    private final SaveManager saveManager = new SaveManager();
    private final WorldBuilder worldBuilder = new WorldBuilder();

    private Map<String, Room> rooms;
    private Map<String, Item> items;
    private Map<String, Npc> npcs;
    private Player player;
    private GameState gameState;
    private boolean running;

    public void start(String[] args) {
        loadFreshWorld();
        if (args != null && args.length > 0 && args[0].equalsIgnoreCase("load")) {
            attemptAutoLoad();
        } else {
            promptToLoadIfSaveExists();
        }

        printIntro();
        describeCurrentRoom(true);

        running = true;
        mainLoop();
    }

    private void mainLoop() {
        while (running) {
            if (gameState.isGameWon()) {
                celebrateVictory();
                break;
            }

            System.out.print("\n> ");
            if (!scanner.hasNextLine()) {
                System.out.println("The world fades as silence answers your last thought.");
                break;
            }
            String input = scanner.nextLine().trim();
            if (input.isBlank()) {
                continue;
            }
            processCommand(input);
        }
    }

    private void processCommand(String rawInput) {
        String normalized = rawInput.trim();
        if (normalized.isEmpty()) {
            return;
        }

        String lower = normalized.toLowerCase(Locale.ROOT);
        List<String> tokens = Arrays.stream(lower.split("\\s+"))
            .filter(token -> !token.isBlank())
            .collect(Collectors.toList());

        String command = tokens.isEmpty() ? lower : tokens.get(0);
        String argument = normalized.length() > command.length()
            ? normalized.substring(command.length()).trim()
            : "";

        switch (command) {
            case "help" -> showHelp();
            case "look", "examine", "inspect" -> handleLook(argument);
            case "go" -> handleGo(argument);
            case "north", "south", "east", "west", "up", "down", "in", "out" -> handleGo(command);
            case "n" -> handleGo("north");
            case "s" -> handleGo("south");
            case "e" -> handleGo("east");
            case "w" -> handleGo("west");
            case "u" -> handleGo("up");
            case "d" -> handleGo("down");
            case "take", "get", "grab" -> handleTake(argument);
            case "drop" -> handleDrop(argument);
            case "inventory", "inv", "i" -> showInventory();
            case "talk", "speak" -> handleTalk(argument);
            case "use" -> handleUse(argument);
            case "search" -> handleSearch();
            case "save" -> handleSave(argument);
            case "load" -> handleLoad(argument);
            case "quit", "exit" -> handleQuit();
            default -> System.out.println("That thought slips away unanswered. Try a different phrasing.");
        }
    }

    private void handleLook(String argument) {
        if (argument.isBlank() || argument.equalsIgnoreCase("around") || argument.equalsIgnoreCase("room")) {
            describeCurrentRoom(true);
            return;
        }

        if (argument.startsWith("at ")) {
            argument = argument.substring(3).trim();
        }

        Room current = getCurrentRoom();
        if (isRoomDark(current) && !hasLightSource()) {
            System.out.println("In the pitch dark you can barely make out the shape of your own hands.");
            return;
        }

        Optional<String> itemId = findItemId(argument, mergeCollections(current.getItems(), player.getInventory()));
        if (itemId.isPresent()) {
            Item item = items.get(itemId.get());
            if (item != null) {
                System.out.println(item.getDescription());
            } else {
                System.out.println("You fail to focus on it.");
            }
            return;
        }

        Optional<String> npcId = findNpcId(argument, current.getNpcs());
        if (npcId.isPresent()) {
            Npc npc = npcs.get(npcId.get());
            if (npc != null) {
                System.out.println(npc.getDescription());
                return;
            }
        }

        System.out.println("Nothing like that draws your eye here.");
    }

    private void handleGo(String argument) {
        if (argument == null || argument.isBlank()) {
            System.out.println("Where do you intend to go?");
            return;
        }

        String direction = normalizeDirection(argument);
        if (direction == null) {
            System.out.println("That direction does not align with any path you see.");
            return;
        }

        Room current = getCurrentRoom();
        String targetRoomId = current.getExits().get(direction);
        if (targetRoomId == null) {
            System.out.println("The way " + direction + " is closed to you.");
            return;
        }

        if (!canEnter(current.getId(), targetRoomId, direction)) {
            return;
        }

        player.setCurrentRoomId(targetRoomId);
        describeCurrentRoom(false);
    }

    private boolean canEnter(String fromRoomId, String toRoomId, String direction) {
        if ("cave_mouth".equals(fromRoomId) && "twisting_cavern".equals(toRoomId)) {
            if (!hasLightSource()) {
                System.out.println("Beyond the threshold is only suffocating dark. Bran muttered something about lighting a lantern.");
                return false;
            }
        }

        if ("twisting_cavern".equals(fromRoomId) && "underground_lake".equals(toRoomId)) {
            if (!hasLightSource()) {
                System.out.println("Without a steady light you would never find your way across the slick stones.");
                return false;
            }
        }

        if ("ruined_tower".equals(fromRoomId) && "tower_entry".equals(toRoomId)) {
            if (!gameState.isFlagSet(FLAG_TOWER_UNLOCKED)) {
                System.out.println("The runes flare and the stone door remains unmoved. Something keyed to their pattern might appease them.");
                return false;
            }
        }

        if ("tower_entry".equals(fromRoomId) && "tower_summit".equals(toRoomId)) {
            if (!gameState.isFlagSet(FLAG_TOWER_UNLOCKED)) {
                System.out.println("The stair shimmers like a mirage. Magic still bars the way upward.");
                return false;
            }
        }

        Room target = rooms.get(toRoomId);
        if (target == null) {
            System.out.println("That path seems to fade from existence as soon as you try to follow it.");
            return false;
        }

        if (isRoomDark(target) && !hasLightSource()) {
            System.out.println("Only blackness waits that way. A lit lantern would help.");
            return false;
        }

        return true;
    }

    private void handleTake(String argument) {
        if (argument == null || argument.isBlank()) {
            System.out.println("What would you like to take?");
            return;
        }

        Room room = getCurrentRoom();
        if (isRoomDark(room) && !hasLightSource()) {
            System.out.println("Fumbling in the dark is a sure way to lose your fingers.");
            return;
        }

        Optional<String> itemIdOpt = findItemId(argument, room.getItems());
        if (itemIdOpt.isEmpty()) {
            System.out.println("Your hands close on nothing by that name.");
            return;
        }
        String itemId = itemIdOpt.get();
        Item item = items.get(itemId);
        if (item == null) {
            System.out.println("That object seems to slip from your memory the moment you grasp it.");
            return;
        }
        if (!item.isPortable()) {
            System.out.println("It's fixed fast; you cannot take it.");
            return;
        }
        if (!room.removeItem(itemId)) {
            System.out.println("It refuses to leave this place.");
            return;
        }
        player.addItem(itemId);
        System.out.println("You take the " + item.getName().toLowerCase(Locale.ROOT) + ".");
    }

    private void handleDrop(String argument) {
        if (argument == null || argument.isBlank()) {
            System.out.println("What would you like to set down?");
            return;
        }
        Optional<String> itemIdOpt = findItemId(argument, player.getInventory());
        if (itemIdOpt.isEmpty()) {
            System.out.println("You aren't carrying anything like that.");
            return;
        }
        String itemId = itemIdOpt.get();
        if (!player.removeItem(itemId)) {
            System.out.println("It refuses to leave your pack.");
            return;
        }
        getCurrentRoom().addItem(itemId);
        Item item = items.get(itemId);
        System.out.println("You leave the " + (item != null ? item.getName().toLowerCase(Locale.ROOT) : "item") + " behind.");
    }

    private void showInventory() {
        List<String> inventory = player.getInventory();
        if (inventory.isEmpty()) {
            System.out.println("Your pack is empty save for the scent of pine.");
            return;
        }
        System.out.println("You carry:");
        for (String itemId : inventory) {
            Item item = items.get(itemId);
            if (item != null) {
                System.out.println(" - " + item.getName());
            } else {
                System.out.println(" - Something indescribable (" + itemId + ")");
            }
        }
    }

    private void handleTalk(String argument) {
        if (argument == null || argument.isBlank()) {
            System.out.println("Who do you wish to address?");
            return;
        }

        if (argument.startsWith("to ")) {
            argument = argument.substring(3).trim();
        }

        Room room = getCurrentRoom();
        if (isRoomDark(room) && !hasLightSource()) {
            System.out.println("You whisper into darkness. Nothing answers.");
            return;
        }

        Optional<String> npcIdOpt = findNpcId(argument, room.getNpcs());
        if (npcIdOpt.isEmpty()) {
            System.out.println("No one by that name turns toward you.");
            return;
        }

        performNpcInteraction(npcIdOpt.get());
    }

    private void performNpcInteraction(String npcId) {
        switch (npcId) {
            case "bran" -> talkToBran();
            case "meryl" -> talkToMeryl();
            case "len" -> talkToLen();
            case "wisp" -> talkToWisp();
            default -> {
                Npc npc = npcs.get(npcId);
                if (npc != null) {
                    System.out.println(npc.getName() + " nods politely, offering no new words.");
                } else {
                    System.out.println("Silence answers.");
                }
            }
        }
    }

    private void talkToBran() {
        if (!gameState.isFlagSet(FLAG_LANTERN_GIVEN)) {
            if (player.hasItem("silver_coin")) {
                player.removeItem("silver_coin");
                player.addItem("lantern");
                gameState.setFlag(FLAG_LANTERN_GIVEN, true);
                System.out.println("""
                    Bran weighs the coin, smiles broadly, and presses a sturdy lantern into your hands.
                    "Keep it fed with courage," he chuckles. "And maybe a bit of oil. Light it before you brave the caverns."
                    """);
            } else {
                System.out.println("""
                    Bran leans on the counter. "Lanterns aren't free, friend. A silver coin would loosen my shelves.
                    Folks say the brook hoards shine if you listen for the glimmer."
                    """);
            }
        } else {
            System.out.println("Bran tips an imaginary hat. \"Lantern's treating you well, I hope. Bring back sunnier tales.\"");
        }
    }

    private void talkToMeryl() {
        if (!gameState.isFlagSet(FLAG_MERYL_GAVE_KEY)) {
            if (player.hasItem("glowcap")) {
                player.removeItem("glowcap");
                player.addItem("rune_key");
                gameState.setFlag(FLAG_MERYL_GAVE_KEY, true);
                System.out.println("""
                    Meryl's eyes widen at the glowcap's light. She sets it gently on a sigiled plate and offers you a rune-key.
                    "The tower's heart sealed itself when the blight rose. This key will wake the runes. Plant whatever you find atop at the Ancient Oak."
                    """);
            } else {
                System.out.println("""
                    Meryl studies you. "The heartseed slumbers above the Ruined Tower. Bring me a glowcap from beneath the hill and I'll unlock the way."
                    """);
            }
        } else {
            System.out.println("""
                Meryl traces a pattern in the air. "All paths bend back to the Ancient Oak. When the heartseed finds soil, the vale will breathe again."
                """);
        }
    }

    private void talkToLen() {
        if (!gameState.isFlagSet(FLAG_LEN_GAVE_SHELL)) {
            gameState.setFlag(FLAG_LEN_GAVE_SHELL, true);
            player.addItem("river_shell");
            System.out.println("""
                Len presses a spiral shell into your palm. "Hold it to your ear if you forget why you walk.
                Heard tell that silver likes to hide where water laughs the loudest."
                """);
        } else {
            System.out.println("Len gazes across the water. \"The river remembers kindness. So do the folk of this vale.\"");
        }
    }

    private void talkToWisp() {
        if (!gameState.isFlagSet(FLAG_WISP_BLESSED)) {
            gameState.setFlag(FLAG_WISP_BLESSED, true);
            System.out.println("""
                The guardian wisp circles you, trailing embers that settle on your shoulders like a mantle.
                "Restore what was severed," it chimes, voice like ringing crystal. "The oak waits to be made whole."
                """);
        } else {
            System.out.println("The wisp's light brightens. \"Carry the heartseed to the waiting roots.\"");
        }
    }

    private void handleUse(String argument) {
        if (argument == null || argument.isBlank()) {
            System.out.println("What do you intend to use?");
            return;
        }

        Optional<String> itemIdOpt = findItemId(argument, player.getInventory());
        if (itemIdOpt.isEmpty()) {
            System.out.println("You don't hold anything by that name.");
            return;
        }

        String itemId = itemIdOpt.get();
        switch (itemId) {
            case "lantern" -> useLantern();
            case "oak_compass" -> System.out.println("The compass spins once, then settles. It hums approval as you face north.");
            case "rune_key" -> useRuneKey();
            case "heartseed" -> useHeartseed();
            case "river_shell" -> System.out.println("You raise the shell. A distant tide answers, reminding you of moonlit promises.");
            default -> {
                Item item = items.get(itemId);
                if (item != null) {
                    System.out.println("You cannot find a meaningful way to use the " + item.getName().toLowerCase(Locale.ROOT) + " here.");
                } else {
                    System.out.println("That seems dull in your hands, without purpose.");
                }
            }
        }
    }

    private void useLantern() {
        if (!player.hasItem("lantern")) {
            System.out.println("You pat your pack, but there's no lantern there.");
            return;
        }
        if (gameState.isFlagSet(FLAG_LANTERN_LIT)) {
            System.out.println("The lantern already burns bright, chasing shadows from your path.");
            return;
        }
        gameState.setFlag(FLAG_LANTERN_LIT, true);
        System.out.println("You strike flint; the lantern's flame flares to life, painting the world in warm gold.");
    }

    private void useRuneKey() {
        Room room = getCurrentRoom();
        if (!"ruined_tower".equals(room.getId())) {
            System.out.println("The rune-key hums softly but the resonance fades without a matching lock.");
            return;
        }
        if (gameState.isFlagSet(FLAG_TOWER_UNLOCKED)) {
            System.out.println("The stone door already stands attuned to your presence.");
            return;
        }
        gameState.setFlag(FLAG_TOWER_UNLOCKED, true);
        System.out.println("""
            You press the rune-key into a recess. Stone shifts like waking muscle, and the door eases open with a sigh of ancient magic.
            """);
    }

    private void useHeartseed() {
        Room room = getCurrentRoom();
        if (!"ancient_oak".equals(room.getId())) {
            System.out.println("The heartseed pulses impatiently. It yearns for the soil beneath the Ancient Oak.");
            return;
        }
        if (!player.hasItem("heartseed")) {
            System.out.println("Your hands are empty of living light.");
            return;
        }
        player.removeItem("heartseed");
        gameState.setGameWon(true);
    }

    private void handleSearch() {
        Room room = getCurrentRoom();
        if (isRoomDark(room) && !hasLightSource()) {
            System.out.println("You paw blindly at the dark. Better to bring light first.");
            return;
        }
        List<String> revealed = room.revealAllHiddenItems();
        if (revealed.isEmpty()) {
            System.out.println("You search carefully but uncover nothing new.");
            return;
        }
        for (String itemId : revealed) {
            Item item = items.get(itemId);
            if (item != null) {
                System.out.println("You uncover a " + item.getName().toLowerCase(Locale.ROOT) + " tucked out of sight.");
            } else {
                System.out.println("Something hidden reveals itself.");
            }
        }
    }

    private void handleSave(String argument) {
        String slot = argument.isBlank() ? null : argument;
        Path savePath = saveManager.resolveSavePath(slot);
        try {
            SaveData data = createSaveData();
            saveManager.save(data, savePath);
            System.out.println("Progress etched into " + savePath.toString() + ".");
        } catch (IOException e) {
            System.out.println("The save sigils sputter: " + e.getMessage());
        }
    }

    private void handleLoad(String argument) {
        String slot = argument.isBlank() ? null : argument;
        Path savePath = saveManager.resolveSavePath(slot);
        try {
            Optional<SaveData> maybeData = saveManager.load(savePath);
            if (maybeData.isEmpty()) {
                System.out.println("No remembered pattern rests within " + savePath.toString() + ".");
                return;
            }
            applySaveData(maybeData.get());
            System.out.println("Memories stir. You stand once more where you left off.");
            describeCurrentRoom(true);
        } catch (IOException e) {
            System.out.println("The load ritual falters: " + e.getMessage());
        }
    }

    private void handleQuit() {
        System.out.print("Would you like to save before you depart? (yes/no) ");
        String response = readLine().map(s -> s.trim().toLowerCase(Locale.ROOT)).orElse("");
        if (response.startsWith("y")) {
            handleSave("");
        }
        System.out.println("You let the vision go. When you return, the vale will wait.");
        running = false;
    }

    private void showHelp() {
        System.out.println("""
            You gather your thoughts:
            - look / examine / inspect : Observe your surroundings or a specific item (e.g., 'look lantern')
            - go <direction> or simply north/south/east/west/up/down/in/out: Travel between locations
            - take <item>, drop <item>: Manage what you carry
            - inventory (or i): Check your belongings
            - talk <name>: Speak with someone nearby
            - use <item>: Attempt to use or activate something you carry
            - search: Comb a room for hidden surprises
            - save [name], load [name]: Preserve or revisit your progress (name optional)
            - quit: Leave the adventure (with a chance to save)
            """);
    }

    private void describeCurrentRoom(boolean forceFull) {
        Room room = getCurrentRoom();
        boolean revisiting = gameState.isVisited(room.getId()) && !forceFull;

        if (isRoomDark(room) && !hasLightSource()) {
            System.out.println("You stumble through oppressive dark. A light would reveal more.");
            return;
        }

        System.out.println();
        System.out.println(room.getName());
        System.out.println(room.getDescription(revisiting));

        gameState.markVisited(room.getId());

        List<String> npcsHere = room.getNpcs();
        if (!npcsHere.isEmpty()) {
            String npcLine = npcsHere.stream()
                .map(npcs::get)
                .filter(Objects::nonNull)
                .map(Npc::getName)
                .collect(Collectors.joining(", "));
            if (!npcLine.isBlank()) {
                System.out.println("You notice: " + npcLine + ".");
            }
        }

        List<String> itemsHere = room.getItems();
        if (!itemsHere.isEmpty()) {
            System.out.println("Items within reach:");
            for (String itemId : itemsHere) {
                Item item = items.get(itemId);
                if (item != null) {
                    System.out.println(" - " + item.getName());
                }
            }
        }

        if (!room.getHiddenItems().isEmpty()) {
            System.out.println("Something about this place suggests more lies concealed here.");
        }

        if (!room.getExits().isEmpty()) {
            String exits = room.getExits().keySet().stream()
                .collect(Collectors.joining(", "));
            System.out.println("Exits: " + exits + ".");
        }
    }

    private void celebrateVictory() {
        System.out.println("""

            As the heartseed sinks into the Ancient Oak's roots, light races up the trunk and across every branch.
            Bells of bone and ivy chime with new life. A warmth spreads outward, lifting the veil of blight from the vale.

            The forest breathes again because you dared to listen, to wander, to believe.
            """);
        System.out.println("Congratulations! You have restored the Verdant Vale.");
    }

    private SaveData createSaveData() {
        SaveData data = new SaveData();
        data.setCurrentRoomId(player.getCurrentRoomId());
        data.setInventory(new ArrayList<>(player.getInventory()));

        Map<String, List<String>> roomItems = new HashMap<>();
        Map<String, List<String>> roomHidden = new HashMap<>();
        for (Map.Entry<String, Room> entry : rooms.entrySet()) {
            roomItems.put(entry.getKey(), new ArrayList<>(entry.getValue().getItems()));
            roomHidden.put(entry.getKey(), new ArrayList<>(entry.getValue().getHiddenItems()));
        }
        data.setRoomItems(roomItems);
        data.setRoomHiddenItems(roomHidden);
        data.setFlags(new HashMap<>(gameState.getFlags()));
        data.setVisitedRooms(new HashSet<>(gameState.getVisitedRooms()));
        data.setGameWon(gameState.isGameWon());
        return data;
    }

    private void applySaveData(SaveData data) {
        loadFreshWorld();

        player.setCurrentRoomId(data.getCurrentRoomId());
        player.setInventory(new ArrayList<>(data.getInventory()));

        for (Map.Entry<String, List<String>> entry : data.getRoomItems().entrySet()) {
            Room room = rooms.get(entry.getKey());
            if (room != null) {
                room.setItems(new ArrayList<>(entry.getValue()));
            }
        }
        for (Map.Entry<String, List<String>> entry : data.getRoomHiddenItems().entrySet()) {
            Room room = rooms.get(entry.getKey());
            if (room != null) {
                room.getHiddenItems().clear();
                room.getHiddenItems().addAll(entry.getValue());
            }
        }
        gameState.getFlags().clear();
        gameState.getFlags().putAll(data.getFlags());
        gameState.getVisitedRooms().clear();
        gameState.getVisitedRooms().addAll(data.getVisitedRooms());
        gameState.setGameWon(data.isGameWon());
    }

    private void loadFreshWorld() {
        GameWorld world = worldBuilder.createWorld();
        rooms = world.getRooms();
        items = world.getItems();
        npcs = world.getNpcs();
        player = world.getPlayer();
        gameState = world.getGameState();
    }

    private void attemptAutoLoad() {
        try {
            Optional<SaveData> maybe = saveManager.loadDefaultIfPresent();
            if (maybe.isEmpty()) {
                System.out.println("No saved echoes answered the call to load.");
                return;
            }
            applySaveData(maybe.get());
            System.out.println("Memories settle around you as the world reforms.");
        } catch (IOException e) {
            System.out.println("Automatic load faltered: " + e.getMessage());
        }
    }

    private void promptToLoadIfSaveExists() {
        Path defaultPath = saveManager.resolveSavePath(null);
        if (!Files.exists(defaultPath)) {
            return;
        }
        System.out.print("A saved journey is nearby. Restore it? (yes/no) ");
        String answer = readLine().map(s -> s.trim().toLowerCase(Locale.ROOT)).orElse("");
        if (!answer.startsWith("y")) {
            return;
        }
        try {
            Optional<SaveData> maybe = saveManager.load(defaultPath);
            if (maybe.isPresent()) {
                applySaveData(maybe.get());
                System.out.println("The vale remembers you. Your steps fall where they once did.");
            } else {
                System.out.println("The save stone cracks; nothing can be read.");
            }
        } catch (IOException e) {
            System.out.println("The save refuses to awaken: " + e.getMessage());
        }
    }

    private void printIntro() {
        System.out.println("""
            The Verdant Vale has grown quiet. Glimmers of blight curl through the roots and the Ancient Oak's bells hang silent.
            Legends whisper that a heartseed waits within the Ruined Tower, sealed away when the world grew dim.

            You wake beneath a canopy of stars, hand wrapped around an oak compass buzzing with expectation.
            The standing stones hum, urging you to step beyond the clearing and mend what was broken.
            """);
    }

    private Room getCurrentRoom() {
        Room room = rooms.get(player.getCurrentRoomId());
        if (room == null) {
            throw new IllegalStateException("Player stands in a place that no longer exists: " + player.getCurrentRoomId());
        }
        return room;
    }

    private boolean isRoomDark(Room room) {
        return room.isDark();
    }

    private boolean hasLightSource() {
        return player.hasItem("lantern") && gameState.isFlagSet(FLAG_LANTERN_LIT);
    }

    private Optional<String> readLine() {
        if (!scanner.hasNextLine()) {
            return Optional.empty();
        }
        return Optional.of(scanner.nextLine());
    }

    private Optional<String> findItemId(String userInput, Collection<String> candidateIds) {
        if (userInput == null || userInput.isBlank()) {
            return Optional.empty();
        }
        String query = userInput.trim().toLowerCase(Locale.ROOT);

        for (String id : candidateIds) {
            Item item = items.get(id);
            if (item == null) {
                continue;
            }
            if (item.getId().equalsIgnoreCase(query) || item.getName().equalsIgnoreCase(userInput.trim())) {
                return Optional.of(item.getId());
            }
        }

        for (String id : candidateIds) {
            Item item = items.get(id);
            if (item == null) {
                continue;
            }
            String lowerName = item.getName().toLowerCase(Locale.ROOT);
            if (lowerName.contains(query) || item.getId().toLowerCase(Locale.ROOT).contains(query)) {
                return Optional.of(item.getId());
            }
        }

        return Optional.empty();
    }

    private Optional<String> findNpcId(String userInput, Collection<String> candidateIds) {
        if (userInput == null || userInput.isBlank()) {
            return Optional.empty();
        }
        String query = userInput.trim().toLowerCase(Locale.ROOT);
        for (String id : candidateIds) {
            Npc npc = npcs.get(id);
            if (npc == null) {
                continue;
            }
            if (npc.getId().equalsIgnoreCase(query) || npc.getName().toLowerCase(Locale.ROOT).equals(query)) {
                return Optional.of(npc.getId());
            }
        }

        for (String id : candidateIds) {
            Npc npc = npcs.get(id);
            if (npc == null) {
                continue;
            }
            String lowerName = npc.getName().toLowerCase(Locale.ROOT);
            if (lowerName.contains(query) || npc.getId().toLowerCase(Locale.ROOT).contains(query)) {
                return Optional.of(npc.getId());
            }
        }

        return Optional.empty();
    }

    private String normalizeDirection(String input) {
        String trimmed = input.trim().toLowerCase(Locale.ROOT);
        return switch (trimmed) {
            case "north", "n" -> "north";
            case "south", "s" -> "south";
            case "east", "e" -> "east";
            case "west", "w" -> "west";
            case "up", "u" -> "up";
            case "down", "d" -> "down";
            case "in", "enter" -> "in";
            case "out", "leave" -> "out";
            default -> null;
        };
    }

    private Collection<String> mergeCollections(Collection<String> a, Collection<String> b) {
        List<String> merged = new ArrayList<>(a);
        merged.addAll(b);
        return merged;
    }
}
