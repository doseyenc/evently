package com.doseyenc.evently.domain.model;

import androidx.annotation.Nullable;

public final class Comment {

    private final String id;
    private final String eventId;
    private final String userId;
    private final String userName;
    @Nullable
    private final String userImageUrl;
    private final String text;
    private final long timestampMillis;
    @Nullable
    private final String parentCommentId;
    private final int likeCount;
    private final boolean isLikedByMe;

    public Comment(String id,
                   String eventId,
                   String userId,
                   String userName,
                   @Nullable String userImageUrl,
                   String text,
                   long timestampMillis,
                   @Nullable String parentCommentId,
                   int likeCount,
                   boolean isLikedByMe) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.text = text;
        this.timestampMillis = timestampMillis;
        this.parentCommentId = parentCommentId;
        this.likeCount = likeCount;
        this.isLikedByMe = isLikedByMe;
    }

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    @Nullable
    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getText() {
        return text;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    @Nullable
    public String getParentCommentId() {
        return parentCommentId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isLikedByMe() {
        return isLikedByMe;
    }

    public Comment withLikeToggled(boolean newIsLikedByMe) {
        int newCount = likeCount + (newIsLikedByMe ? 1 : -1);
        return new Comment(id, eventId, userId, userName, userImageUrl, text, timestampMillis,
                parentCommentId, Math.max(0, newCount), newIsLikedByMe);
    }
}