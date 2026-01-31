package com.doseyenc.evently.domain.model;

import androidx.annotation.NonNull;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return dateMillis == event.dateMillis
                && java.util.Objects.equals(id, event.id)
                && java.util.Objects.equals(title, event.title)
                && java.util.Objects.equals(shortDescription, event.shortDescription)
                && java.util.Objects.equals(imageUrl, event.imageUrl)
                && java.util.Objects.equals(location, event.location);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, title, shortDescription, imageUrl, dateMillis, location);
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{id='" + id + "', title='" + title + "', dateMillis=" + dateMillis + "}";
    }
}
