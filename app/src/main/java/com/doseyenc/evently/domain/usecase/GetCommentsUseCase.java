package com.doseyenc.evently.domain.usecase;

import com.doseyenc.evently.domain.model.Comment;
import com.doseyenc.evently.domain.repository.CommentRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class GetCommentsUseCase {

    private final CommentRepository commentRepository;

    @Inject
    public GetCommentsUseCase(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Single<List<Comment>> execute(String eventId) {
        if (eventId == null) return Single.just(Collections.emptyList());
        return commentRepository.getComments(eventId)
                .onErrorReturnItem(Collections.emptyList());
    }
}
