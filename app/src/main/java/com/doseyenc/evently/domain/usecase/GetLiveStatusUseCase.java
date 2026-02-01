package com.doseyenc.evently.domain.usecase;

import com.doseyenc.evently.domain.repository.LiveStatusRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

public class GetLiveStatusUseCase {

    private final LiveStatusRepository liveStatusRepository;

    @Inject
    public GetLiveStatusUseCase(LiveStatusRepository liveStatusRepository) {
        this.liveStatusRepository = liveStatusRepository;
    }

    public Observable<String> execute(String eventId) {
        if (eventId == null) return Observable.error(new IllegalArgumentException("eventId is null"));
        return liveStatusRepository.getLiveStatus(eventId);
    }
}
