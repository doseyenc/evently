package com.doseyenc.evently.domain.repository;

import com.doseyenc.evently.domain.model.Participant;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface ParticipantRepository {

    Single<List<Participant>> getParticipants(String eventId);
}
