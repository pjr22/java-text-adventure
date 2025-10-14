package com.adventure;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class GameState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, Boolean> flags = new HashMap<>();
    private final Set<String> visitedRooms = new HashSet<>();
    private boolean gameWon;

    public boolean isFlagSet(String flag) {
        return flags.getOrDefault(flag, false);
    }

    public void setFlag(String flag, boolean value) {
        flags.put(flag, value);
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    public Set<String> getVisitedRooms() {
        return visitedRooms;
    }

    public boolean isVisited(String roomId) {
        return visitedRooms.contains(roomId);
    }

    public void markVisited(String roomId) {
        visitedRooms.add(roomId);
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }
}

