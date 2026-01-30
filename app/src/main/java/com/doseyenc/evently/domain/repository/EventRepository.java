package com.doseyenc.evently.domain.repository;

import androidx.annotation.Nullable;

import com.doseyenc.evently.domain.model.Comment;
import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.domain.model.Participant;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface EventRepository {

    Single<List<Event>> getEvents();

    Single<Event> getEventById(String eventId);

    Single<List<Participant>> getParticipants(String eventId);

    Single<List<Comment>> getComments(String eventId);

    /**
     * parentCommentId dolu ise reply null se normal yorum
     * */
    Single<Comment> addComment(String eventId, String text, @Nullable String parentCommentId);

    Single<Comment> toggleCommentLike(String commentId);

    Observable<String> getLiveStatus(String eventId);

    Single<String> getCurrentLiveStatus(String eventId);
}
