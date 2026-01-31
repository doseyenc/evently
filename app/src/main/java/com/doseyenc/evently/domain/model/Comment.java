package com.doseyenc.evently.domain.model;

import androidx.annotation.NonNull;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return timestampMillis == comment.timestampMillis
                && likeCount == comment.likeCount
                && isLikedByMe == comment.isLikedByMe
                && java.util.Objects.equals(id, comment.id)
                && java.util.Objects.equals(eventId, comment.eventId)
                && java.util.Objects.equals(userId, comment.userId)
                && java.util.Objects.equals(userName, comment.userName)
                && java.util.Objects.equals(userImageUrl, comment.userImageUrl)
                && java.util.Objects.equals(text, comment.text)
                && java.util.Objects.equals(parentCommentId, comment.parentCommentId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, eventId, userId, userName, userImageUrl, text,
                timestampMillis, parentCommentId, likeCount, isLikedByMe);
    }

    @NonNull
    @Override
    public String toString() {
        return "Comment{id='" + id + "', userName='" + userName + "', text='" + text + "'}";
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String eventId;
        private String userId;
        private String userName;
        private String userImageUrl;
        private String text;
        private long timestampMillis;
        private String parentCommentId;
        private int likeCount;
        private boolean isLikedByMe;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder userImageUrl(String userImageUrl) {
            this.userImageUrl = userImageUrl;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder timestampMillis(long timestampMillis) {
            this.timestampMillis = timestampMillis;
            return this;
        }

        public Builder parentCommentId(String parentCommentId) {
            this.parentCommentId = parentCommentId;
            return this;
        }

        public Builder likeCount(int likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public Builder isLikedByMe(boolean isLikedByMe) {
            this.isLikedByMe = isLikedByMe;
            return this;
        }

        public Comment build() {
            return new Comment(id, eventId, userId, userName, userImageUrl, text, timestampMillis,
                    parentCommentId, likeCount, isLikedByMe);
        }
    }
}