package com.adventure;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Player implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String currentRoomId;
    private final List<String> inventory = new ArrayList<>();

    public Player(String startingRoomId) {
        this.currentRoomId = startingRoomId;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public List<String> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public void setInventory(List<String> itemIds) {
        inventory.clear();
        inventory.addAll(itemIds);
    }

    public boolean hasItem(String itemId) {
        return inventory.stream().anyMatch(id -> id.equalsIgnoreCase(itemId));
    }

    public void addItem(String itemId) {
        inventory.add(itemId);
    }

    public boolean removeItem(String itemId) {
        return inventory.removeIf(id -> id.equalsIgnoreCase(itemId));
    }
}

