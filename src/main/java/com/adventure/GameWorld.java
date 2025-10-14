package com.adventure;

import java.util.Map;

public final class GameWorld {
    private final Map<String, Room> rooms;
    private final Map<String, Item> items;
    private final Map<String, Npc> npcs;
    private final Player player;
    private final GameState gameState;

    public GameWorld(Map<String, Room> rooms, Map<String, Item> items, Map<String, Npc> npcs, Player player, GameState gameState) {
        this.rooms = rooms;
        this.items = items;
        this.npcs = npcs;
        this.player = player;
        this.gameState = gameState;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Map<String, Item> getItems() {
        return items;
    }

    public Map<String, Npc> getNpcs() {
        return npcs;
    }

    public Player getPlayer() {
        return player;
    }

    public GameState getGameState() {
        return gameState;
    }
}

