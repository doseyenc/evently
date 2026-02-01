package com.doseyenc.evently.ui.detail;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.doseyenc.evently.R;
import com.doseyenc.evently.domain.model.Comment;
import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.domain.model.Participant;
import com.doseyenc.evently.domain.usecase.AddCommentUseCase;
import com.doseyenc.evently.domain.usecase.GetCurrentLiveStatusUseCase;
import com.doseyenc.evently.domain.usecase.GetEventDetailUseCase;
import com.doseyenc.evently.domain.usecase.GetLiveStatusUseCase;
import com.doseyenc.evently.domain.usecase.ToggleCommentLikeUseCase;
import com.doseyenc.evently.ui.base.BaseViewModel;
import com.doseyenc.evently.ui.base.SingleLiveEvent;
import com.doseyenc.evently.ui.base.ViewState;
import com.doseyenc.evently.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class EventDetailViewModel extends BaseViewModel implements CommentItemHandler {

    private final GetEventDetailUseCase getEventDetailUseCase;
    private final AddCommentUseCase addCommentUseCase;
    private final ToggleCommentLikeUseCase toggleCommentLikeUseCase;
    private final GetLiveStatusUseCase getLiveStatusUseCase;
    private final GetCurrentLiveStatusUseCase getCurrentLiveStatusUseCase;

    private final MutableLiveData<ViewState<Event>> eventState = new MutableLiveData<>();
    private final MutableLiveData<List<Participant>> participants = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Comment>> comments = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> dateLocationText = new MutableLiveData<>("");
    private final MutableLiveData<String> liveStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showLiveBanner = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> countdownSeconds = new MutableLiveData<>(Constants.LiveStatus.COUNTDOWN_SECONDS);
    private final SingleLiveEvent<Comment> replyClickEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> commentPostedEvent = new SingleLiveEvent<>();

    public final ObservableField<String> draftCommentText = new ObservableField<>("");
    private String replyingToCommentId;
    public final ObservableField<String> participantSearchQuery = new ObservableField<>("");
    private final MediatorLiveData<List<Participant>> filteredParticipants = new MediatorLiveData<>();
    private final MutableLiveData<Integer> participantsEmptyMessageResId = new MutableLiveData<>(0);
    private List<Participant> fullParticipantList = new ArrayList<>();

    private String eventId;
    private Disposable countdownDisposable;

    public LiveData<ViewState<Event>> getEventState() {
        return eventState;
    }

    public LiveData<List<Participant>> getFilteredParticipants() {
        return filteredParticipants;
    }

    public LiveData<Integer> getParticipantsEmptyMessageResId() {
        return participantsEmptyMessageResId;
    }

    public LiveData<List<Comment>> getComments() {
        return comments;
    }

    public LiveData<String> getDateLocationText() {
        return dateLocationText;
    }

    public LiveData<String> getLiveStatus() {
        return liveStatus;
    }

    public LiveData<Boolean> getShowLiveBanner() {
        return showLiveBanner;
    }

    public LiveData<Integer> getCountdownSeconds() {
        return countdownSeconds;
    }

    public LiveData<Comment> getReplyClickEvent() {
        return replyClickEvent;
    }

    public LiveData<Boolean> getCommentPostedEvent() {
        return commentPostedEvent;
    }

    @Inject
    public EventDetailViewModel(
            GetEventDetailUseCase getEventDetailUseCase,
            AddCommentUseCase addCommentUseCase,
            ToggleCommentLikeUseCase toggleCommentLikeUseCase,
            GetLiveStatusUseCase getLiveStatusUseCase,
            GetCurrentLiveStatusUseCase getCurrentLiveStatusUseCase) {
        this.getEventDetailUseCase = getEventDetailUseCase;
        this.addCommentUseCase = addCommentUseCase;
        this.toggleCommentLikeUseCase = toggleCommentLikeUseCase;
        this.getLiveStatusUseCase = getLiveStatusUseCase;
        this.getCurrentLiveStatusUseCase = getCurrentLiveStatusUseCase;
        filteredParticipants.addSource(participants, list -> {
            fullParticipantList = list != null ? list : new ArrayList<>();
            applyParticipantFilter();
        });
        participantSearchQuery.addOnPropertyChangedCallback(new androidx.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(androidx.databinding.Observable sender, int propertyId) {
                applyParticipantFilter();
            }
        });
    }

    public void init(String eventId) {
        if (this.eventId != null) return;
        this.eventId = eventId;
        loadDetail();
        subscribeLiveStatus();
    }

    private void applyParticipantFilter() {
        String query = participantSearchQuery.get() != null ? participantSearchQuery.get().trim() : "";
        List<Participant> filtered;
        if (query.isEmpty()) {
            filtered = new ArrayList<>(fullParticipantList);
        } else {
            String lower = query.toLowerCase();
            filtered = new ArrayList<>();
            for (Participant p : fullParticipantList) {
                if ((p.getName() != null && p.getName().toLowerCase().contains(lower))
                        || (p.getRole() != null && p.getRole().toLowerCase().contains(lower))) {
                    filtered.add(p);
                }
            }
        }
        filteredParticipants.setValue(filtered);
        int messageResId;
        if (fullParticipantList.isEmpty()) {
            messageResId = R.string.participants_empty;
        } else if (filtered.isEmpty()) {
            messageResId = R.string.participants_search_empty;
        } else {
            messageResId = 0;
        }
        participantsEmptyMessageResId.setValue(messageResId);
    }

    public void postComment() {
        String text = draftCommentText.get() != null ? draftCommentText.get().trim() : "";
        if (eventId == null || text.isEmpty()) return;
        String parentId = replyingToCommentId;
        addDisposable(
                addCommentUseCase.execute(eventId, text, parentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                comment -> {
                                    List<Comment> list = new ArrayList<>(comments.getValue() != null ? comments.getValue() : new ArrayList<>());
                                    list.add(0, comment);
                                    comments.setValue(list);
                                    draftCommentText.set("");
                                    replyingToCommentId = null;
                                    commentPostedEvent.setValue(true);
                                },
                                error -> Log.e("EventDetailViewModel", error.getMessage() != null ? error.getMessage() : "addComment failed")
                        )
        );
    }

    @Override
    public void onLikeClick(Comment comment) {
        toggleCommentLike(comment.getId());
    }

    @Override
    public void onReplyClick(Comment comment) {
        replyClickEvent.setValue(comment);
        replyingToCommentId = comment != null ? comment.getId() : null;
    }

    public void startCountdown() {
        stopCountdown();
        countdownDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(tick -> Constants.LiveStatus.COUNTDOWN_SECONDS - (int) (tick % Constants.LiveStatus.COUNTDOWN_SECONDS))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        seconds -> {
                            countdownSeconds.setValue(seconds);
                            if (seconds == 0) refreshLiveStatus();
                        },
                        error -> {
                            error.getMessage();
                            Log.e("EventDetailViewModel", Objects.requireNonNull(error.getMessage()));
                        }
                );
        addDisposable(countdownDisposable);
    }

    public void stopCountdown() {
        if (countdownDisposable != null && !countdownDisposable.isDisposed()) {
            countdownDisposable.dispose();
        }
    }

    private void loadDetail() {
        if (eventId == null) return;
        eventState.setValue(ViewState.loading());

        addDisposable(
                getEventDetailUseCase.execute(eventId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    Event event = result.getEvent();
                                    dateLocationText.setValue(formatDateLocation(event.getDateMillis(), event.getLocation()));
                                    eventState.setValue(ViewState.success(event));
                                    participants.setValue(result.getParticipants());
                                    comments.setValue(result.getComments());
                                },
                                error -> eventState.setValue(ViewState.error(
                                        error.getMessage() != null ? error.getMessage() : "Unknown error"))
                        )
        );
    }

    private void subscribeLiveStatus() {
        if (eventId == null) return;
        addDisposable(
                getLiveStatusUseCase.execute(eventId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                status -> {
                                    liveStatus.setValue(status);
                                    showLiveBanner.setValue(true);
                                },
                                error -> showLiveBanner.setValue(false)
                        )
        );
    }

    public void refreshLiveStatus() {
        if (eventId == null) return;
        addDisposable(
                getCurrentLiveStatusUseCase.execute(eventId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                status -> {
                                    liveStatus.setValue(status);
                                    showLiveBanner.setValue(true);
                                    startCountdown();
                                },
                                error -> {
                                    error.getMessage();
                                    Log.e("EventDetailViewModel", Objects.requireNonNull(error.getMessage()));
                                }
                        )
        );
    }

    public void toggleCommentLike(String commentId) {
        if (commentId == null) return;
        addDisposable(
                toggleCommentLikeUseCase.execute(commentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                updated -> {
                                    List<Comment> list = comments.getValue();
                                    if (list == null) return;
                                    List<Comment> newList = new ArrayList<>();
                                    for (Comment c : list) {
                                        newList.add(c.getId().equals(commentId) ? updated : c);
                                    }
                                    comments.setValue(newList);
                                },
                                error -> {
                                    error.getMessage();
                                    Log.e("EventDetailViewModel", Objects.requireNonNull(error.getMessage()));
                                }
                        )
        );
    }

    private static String formatDateLocation(long dateMillis, String location) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String datePart = (month != null ? month : "") + " " + day;
        return (location != null && !location.isEmpty()) ? datePart + " • " + location : datePart;
    }
}
