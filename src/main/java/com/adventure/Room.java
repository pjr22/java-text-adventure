package com.adventure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Room {
    private final String id;
    private final String name;
    private final String description;
    private final String revisitedDescription;
    private final boolean dark;
    private final Map<String, String> exits = new LinkedHashMap<>();
    private final List<String> items = new ArrayList<>();
    private final List<String> hiddenItems = new ArrayList<>();
    private final List<String> npcs = new ArrayList<>();

    public Room(String id, String name, String description, String revisitedDescription, boolean dark) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.revisitedDescription = revisitedDescription;
        this.dark = dark;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription(boolean revisiting) {
        if (revisiting && revisitedDescription != null && !revisitedDescription.isBlank()) {
            return revisitedDescription;
        }
        return description;
    }

    public boolean isDark() {
        return dark;
    }

    public Map<String, String> getExits() {
        return exits;
    }

    public void addExit(String direction, String targetRoomId) {
        exits.put(direction, targetRoomId);
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> itemIds) {
        items.clear();
        items.addAll(itemIds);
    }

    public void addItem(String itemId) {
        items.add(itemId);
    }

    public boolean removeItem(String itemId) {
        return items.removeIf(id -> id.equalsIgnoreCase(itemId));
    }

    public void addHiddenItem(String itemId) {
        hiddenItems.add(itemId);
    }

    public Optional<String> revealHiddenItem(String itemNameOrId, Map<String, Item> catalog) {
        for (String hiddenId : new ArrayList<>(hiddenItems)) {
            Item item = catalog.get(hiddenId);
            if (item == null) {
                continue;
            }
            if (item.getId().equalsIgnoreCase(itemNameOrId) || item.getName().equalsIgnoreCase(itemNameOrId)) {
                hiddenItems.remove(hiddenId);
                items.add(hiddenId);
                return Optional.of(hiddenId);
            }
        }
        return Optional.empty();
    }

    public List<String> getHiddenItems() {
        return hiddenItems;
    }

    public List<String> revealAllHiddenItems() {
        List<String> revealed = new ArrayList<>(hiddenItems);
        items.addAll(revealed);
        hiddenItems.clear();
        return revealed;
    }

    public List<String> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<String> npcIds) {
        npcs.clear();
        npcs.addAll(npcIds);
    }

    public void addNpc(String npcId) {
        npcs.add(npcId);
    }
}
