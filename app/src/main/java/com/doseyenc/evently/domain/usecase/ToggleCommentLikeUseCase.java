package com.doseyenc.evently.domain.usecase;

import com.doseyenc.evently.domain.model.Comment;
import com.doseyenc.evently.domain.repository.CommentRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class ToggleCommentLikeUseCase {

    private final CommentRepository commentRepository;

    @Inject
    public ToggleCommentLikeUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Single<Comment> execute(String commentId) {
        if (commentId == null) return Single.error(new IllegalArgumentException("commentId is null"));
        return commentRepository.toggleCommentLike(commentId);
    }
}
