package com.doseyenc.evently.domain.usecase;

import com.doseyenc.evently.domain.repository.LiveStatusRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class GetCurrentLiveStatusUseCase {

    private final LiveStatusRepository liveStatusRepository;

    @Inject
    public GetCurrentLiveStatusUseCase(LiveStatusRepository liveStatusRepository) {
        this.liveStatusRepository = liveStatusRepository;
    }

    public Single<String> execute(String eventId) {
        if (eventId == null) return Single.error(new IllegalArgumentException("eventId is null"));
        return liveStatusRepository.getCurrentLiveStatus(eventId);
    }
}
