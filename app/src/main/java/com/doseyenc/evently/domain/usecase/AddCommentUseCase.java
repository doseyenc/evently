package com.doseyenc.evently.domain.usecase;

import androidx.annotation.Nullable;

import com.doseyenc.evently.domain.model.Comment;
import com.doseyenc.evently.domain.repository.CommentRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class AddCommentUseCase {

    private final CommentRepository commentRepository;

    @Inject
    public AddCommentUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Single<Comment> execute(String eventId, String text, @Nullable String parentCommentId) {
        if (eventId == null || text == null || text.trim().isEmpty()) {
            return Single.error(new IllegalArgumentException("eventId and non-empty text required"));
        }
        return commentRepository.addComment(eventId, text.trim(), parentCommentId);
    }
}