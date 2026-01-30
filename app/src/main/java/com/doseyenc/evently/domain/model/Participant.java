package com.doseyenc.evently.domain.model;

import androidx.annotation.Nullable;

public final class Participant {

    private final String id;
    private final String name;
    private final String role;
    @Nullable
    private final String imageUrl;

    public Participant(String id, String name, String role, @Nullable String imageUrl) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }
}
