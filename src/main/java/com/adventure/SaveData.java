package com.adventure;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SaveData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String currentRoomId;
    private List<String> inventory;
    private Map<String, List<String>> roomItems;
    private Map<String, List<String>> roomHiddenItems;
    private Map<String, Boolean> flags;
    private Set<String> visitedRooms;
    private boolean gameWon;

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public void setInventory(List<String> inventory) {
        this.inventory = inventory;
    }

    public Map<String, List<String>> getRoomItems() {
        if (roomItems == null) {
            roomItems = new HashMap<>();
        }
        return roomItems;
    }

    public void setRoomItems(Map<String, List<String>> roomItems) {
        this.roomItems = roomItems;
    }

    public Map<String, List<String>> getRoomHiddenItems() {
        if (roomHiddenItems == null) {
            roomHiddenItems = new HashMap<>();
        }
        return roomHiddenItems;
    }

    public void setRoomHiddenItems(Map<String, List<String>> roomHiddenItems) {
        this.roomHiddenItems = roomHiddenItems;
    }

    public Map<String, Boolean> getFlags() {
        if (flags == null) {
            flags = new HashMap<>();
        }
        return flags;
    }

    public void setFlags(Map<String, Boolean> flags) {
        this.flags = flags;
    }

    public Set<String> getVisitedRooms() {
        if (visitedRooms == null) {
            visitedRooms = new HashSet<>();
        }
        return visitedRooms;
    }

    public void setVisitedRooms(Set<String> visitedRooms) {
        this.visitedRooms = visitedRooms;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }
}

