package com.doseyenc.evently.domain.usecase;

import com.doseyenc.evently.domain.model.Comment;
import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.domain.model.Participant;

import java.util.List;

public final class EventDetailResult {

    private final Event event;
    private final List<Participant> participants;
    private final List<Comment> comments;

    public EventDetailResult(Event event, List<Participant> participants, List<Comment> comments) {
        this.event = event;
        this.participants = participants != null ? participants : java.util.Collections.emptyList();
        this.comments = comments != null ? comments : java.util.Collections.emptyList();
    }

    public Event getEvent() {
        return event;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
