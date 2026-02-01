package com.doseyenc.evently.domain.repository;

import com.doseyenc.evently.domain.model.Event;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface EventRepository {

    Single<List<Event>> getEvents();

    Single<Event> getEventById(String eventId);
}
