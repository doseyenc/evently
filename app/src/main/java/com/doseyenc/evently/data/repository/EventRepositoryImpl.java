package com.doseyenc.evently.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.doseyenc.evently.domain.model.Comment;
import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.domain.model.Participant;
import com.doseyenc.evently.domain.repository.EventRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public final class EventRepositoryImpl implements EventRepository {

    private static final String CURRENT_USER_ID = "me";
    private static final String CURRENT_USER_NAME = "Cagri";

    private static final String[] LIVE_STATUS_MESSAGES = {
            "Event is about to start",
            "Keynote is in progress",
            "Coffee break",
            "Q&A session started",
            "Event finished"
    };

    private final List<Event> events;
    private final Map<String, List<Participant>> participantsByEventId;
    private final Map<String, List<Comment>> commentsByEventId;
    private final AtomicInteger liveStatusIndex = new AtomicInteger(0);

    @Inject
    public EventRepositoryImpl() {
        this.events = createMockEvents();
        this.participantsByEventId = createMockParticipants();
        this.commentsByEventId = createMockComments();
    }

    @Override
    public Single<List<Event>> getEvents() {
        return Single.just(events);
    }

    @Override
    public Single<Event> getEventById(String eventId) {
        return Single.fromCallable(() -> {
            for (Event e : events) {
                if (e.getId().equals(eventId)) return e;
            }
            throw new IllegalArgumentException("Event not found: " + eventId);
        });
    }

    @Override
    public Single<List<Participant>> getParticipants(String eventId) {
        List<Participant> list = participantsByEventId.get(eventId);
        List<Participant> result = list != null ? new ArrayList<>(list) : new ArrayList<>();
        return Single.just(result);
    }

    @Override
    public Single<List<Comment>> getComments(String eventId) {
        List<Comment> list = commentsByEventId.get(eventId);
        if (list == null) {
            return Single.just(Collections.emptyList());
        }
        List<Comment> result;
        synchronized (list) {
            result = new ArrayList<>(list);
        }
        return Single.just(result);
    }

    @Override
    public Single<Comment> addComment(String eventId, String text, @Nullable String parentCommentId) {
        return Single.fromCallable(() -> {
            List<Comment> list = commentsByEventId.computeIfAbsent(eventId, k -> new ArrayList<>());
            Comment comment = new Comment(
                    "c_" + System.currentTimeMillis(), eventId, CURRENT_USER_ID, CURRENT_USER_NAME,
                    null, text, System.currentTimeMillis(), parentCommentId, 0, false
            );
            synchronized (list) {
                list.add(comment);
            }
            return comment;
        });
    }

    @Override
    public Single<Comment> toggleCommentLike(String commentId) {
        return Single.fromCallable(() -> {
            for (List<Comment> list : commentsByEventId.values()) {
                for (int i = 0; i < list.size(); i++) {
                    Comment c = list.get(i);
                    if (c.getId().equals(commentId)) {
                        Comment updated = c.withLikeToggled(!c.isLikedByMe());
                        list.set(i, updated);
                        return updated;
                    }
                }
            }
            throw new IllegalArgumentException("Comment not found: " + commentId);
        });
    }

    @Override
    public Observable<String> getLiveStatus(String eventId) {
        return Observable.interval(0, 30, TimeUnit.SECONDS)
                .map(idx -> LIVE_STATUS_MESSAGES[(int) (idx % LIVE_STATUS_MESSAGES.length)]);
    }

    @Override
    public Single<String> getCurrentLiveStatus(String eventId) {
        return Single.fromCallable(() -> {
            int idx = liveStatusIndex.getAndIncrement() % LIVE_STATUS_MESSAGES.length;
            if (idx < 0) idx += LIVE_STATUS_MESSAGES.length;
            return LIVE_STATUS_MESSAGES[idx];
        });
    }

    private List<Event> createMockEvents() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long todayDate = cal.getTimeInMillis();
        long oneDayMs = 86400000L;

        long pastDate = todayDate - 2 * oneDayMs;   // 2 gün önce
        long futureDate = todayDate + oneDayMs;     // yarın

        return getEventList(todayDate, futureDate, pastDate);
    }

    @NonNull
    private static List<Event> getEventList(long todayDate, long futureDate, long pastDate) {
        List<Event> list = new ArrayList<>();
        list.add(new Event("e1", "Global Tech Summit 2024", "Leading tech conference in San Francisco.",
                null, todayDate, "San Francisco, CA • Nov 12-14"));
        list.add(new Event("e2", "Local Charity Marathon", "Annual charity run through the city.",
                null, futureDate, "City Center • Oct 25"));
        list.add(new Event("e3", "Modern Art Exhibition", "Contemporary art from local artists.",
                null, pastDate, "Gallery District • Oct 26"));
        return list;
    }

    private Map<String, List<Participant>> createMockParticipants() {
        Map<String, List<Participant>> map = new ConcurrentHashMap<>();
        List<Participant> list = new ArrayList<>();
        list.add(new Participant("p1", "Alex Rivera", "Senior Developer @ TechCorp", null));
        list.add(new Participant("p2", "Sarah Chen", "Product Manager @ Innovate", null));
        list.add(new Participant("p3", "Marcus Johnson", "Design Lead", null));
        list.add(new Participant("p4", "Elena Rodriguez", "DevOps Engineer", null));
        list.add(new Participant("p5", "David Kim", "Backend Developer", null));
        list.add(new Participant("p6", "Maya Patel", "UX Researcher", null));
        map.put("e1", list);
        map.put("e2", new ArrayList<>());
        map.put("e3", new ArrayList<>());
        return map;
    }

    private Map<String, List<Comment>> createMockComments() {
        Map<String, List<Comment>> map = new ConcurrentHashMap<>();
        long base = System.currentTimeMillis() - 3600000L;
        List<Comment> list = new ArrayList<>();
        list.add(new Comment("c1", "e1", "u1", "Alex Rivera", null,
                "Looking forward to the keynote!", base, null, 12, false));
        list.add(new Comment("c2", "e1", "u2", "Sarah Jenkins", null,
                "Same here, the lineup is great.", base + 60000, null, 5, false));
        list.add(new Comment("c3", "e1", "u3", "Marcus Chen", null,
                "Agreed!", base + 120000, "c2", 2, false));
        list.add(new Comment("c4", "e1", "u4", "Elena Rodriguez", null,
                "See you all there!", base + 180000, null, 0, false));
        map.put("e1", list);
        map.put("e2", new ArrayList<>());
        map.put("e3", new ArrayList<>());
        return map;
    }
}
