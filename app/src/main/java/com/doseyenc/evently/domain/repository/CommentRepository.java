package com.doseyenc.evently.domain.repository;

import androidx.annotation.Nullable;

import com.doseyenc.evently.domain.model.Comment;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface CommentRepository {

    Single<List<Comment>> getComments(String eventId);

    Single<Comment> addComment(String eventId, String text, @Nullable String parentCommentId);

    Single<Comment> toggleCommentLike(String commentId);
}
