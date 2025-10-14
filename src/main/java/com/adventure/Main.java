package com.adventure;

public final class Main {
    private Main() {
        // Utility class
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start(args);
    }
}

