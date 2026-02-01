package com.doseyenc.evently.domain.usecase;

import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.domain.repository.EventRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class GetEventUseCase {

    private final EventRepository eventRepository;

    @Inject
    public GetEventUseCase(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Single<Event> execute(String eventId) {
        if (eventId == null) return Single.error(new IllegalArgumentException("eventId is null"));
        return eventRepository.getEventById(eventId);
    }
}
