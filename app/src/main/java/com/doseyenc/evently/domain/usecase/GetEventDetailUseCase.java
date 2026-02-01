package com.doseyenc.evently.domain.usecase;

import com.doseyenc.evently.domain.repository.CommentRepository;
import com.doseyenc.evently.domain.repository.EventRepository;
import com.doseyenc.evently.domain.repository.ParticipantRepository;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class GetEventDetailUseCase {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final CommentRepository commentRepository;

    @Inject
    public GetEventDetailUseCase(EventRepository eventRepository,
                                 ParticipantRepository participantRepository,
                                 CommentRepository commentRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.commentRepository = commentRepository;
    }

    public Single<EventDetailResult> execute(String eventId) {
        if (eventId == null) return Single.error(new IllegalArgumentException("eventId is null"));
        return eventRepository.getEventById(eventId)
                .flatMap(event -> Single.zip(
                        Single.just(event),
                        participantRepository.getParticipants(eventId),
                        commentRepository.getComments(eventId),
                        (e, p, c) -> new EventDetailResult(e, p != null ? p : new ArrayList<>(), c != null ? c : new ArrayList<>())
                ));
    }
}
