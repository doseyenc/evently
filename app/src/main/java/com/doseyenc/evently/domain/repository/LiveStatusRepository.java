package com.doseyenc.evently.domain.repository;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface LiveStatusRepository {

    Observable<String> getLiveStatus(String eventId);

    Single<String> getCurrentLiveStatus(String eventId);
}
