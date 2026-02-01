package com.doseyenc.evently.domain.usecase;

import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.domain.repository.EventRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class GetEventsUseCase {

    private final EventRepository repository;

    @Inject
    public GetEventsUseCase(EventRepository repository) {
        this.repository = repository;
    }

    public Single<List<Event>> execute() {
        return repository.getEvents();
    }
}
