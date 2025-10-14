package com.adventure;

import java.io.Serial;
import java.io.Serializable;

public final class Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String description;
    private final boolean portable;

    public Item(String id, String name, String description, boolean portable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.portable = portable;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPortable() {
        return portable;
    }
}

