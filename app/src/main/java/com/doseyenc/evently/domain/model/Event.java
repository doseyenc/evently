package com.doseyenc.evently.domain.model;

import androidx.annotation.Nullable;

public final class Event {

    private final String id;
    private final String title;
    private final String shortDescription;
    @Nullable
    private final String imageUrl;
    private final long dateMillis;
    private final String location;

    public Event(String id, String title, String shortDescription,
                 @Nullable String imageUrl, long dateMillis, String location) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.dateMillis = dateMillis;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public String getLocation() {
        return location;
    }
}
