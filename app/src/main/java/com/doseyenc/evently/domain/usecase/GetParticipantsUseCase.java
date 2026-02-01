package com.doseyenc.evently.domain.usecase;

import com.doseyenc.evently.domain.model.Participant;
import com.doseyenc.evently.domain.repository.ParticipantRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class GetParticipantsUseCase {

    private final ParticipantRepository participantRepository;

    @Inject
    public GetParticipantsUseCase(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public Single<List<Participant>> execute(String eventId) {
        if (eventId == null) return Single.just(Collections.emptyList());
        return participantRepository.getParticipants(eventId)
                .onErrorReturnItem(Collections.emptyList());
    }
}
