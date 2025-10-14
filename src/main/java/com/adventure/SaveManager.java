package com.adventure;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class SaveManager {
    private static final String DEFAULT_SAVE_NAME = "savegame.dat";

    private final Path saveDirectory;

    public SaveManager() {
        this(Paths.get("saves"));
    }

    public SaveManager(Path saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public Path resolveSavePath(String userInput) {
        String fileName = (userInput == null || userInput.isBlank()) ? DEFAULT_SAVE_NAME : sanitizeName(userInput) + ".dat";
        return saveDirectory.resolve(fileName);
    }

    public void save(SaveData data, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(data);
        }
    }

    public Optional<SaveData> load(Path path) throws IOException {
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            Object obj = in.readObject();
            if (obj instanceof SaveData save) {
                return Optional.of(save);
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to read save data: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<SaveData> loadDefaultIfPresent() throws IOException {
        Path defaultPath = resolveSavePath(null);
        return load(defaultPath);
    }

    private String sanitizeName(String raw) {
        return raw.trim()
            .replaceAll("[^a-zA-Z0-9-_]", "_")
            .replaceAll("_+", "_");
    }
}

