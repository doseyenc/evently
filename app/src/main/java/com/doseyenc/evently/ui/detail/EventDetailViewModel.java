package com.doseyenc.evently.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.domain.usecase.GetEventUseCase;
import com.doseyenc.evently.ui.base.BaseViewModel;
import com.doseyenc.evently.ui.base.ViewState;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class EventDetailViewModel extends BaseViewModel {

    private final GetEventUseCase getEventUseCase;

    private final MutableLiveData<ViewState<Event>> eventState = new MutableLiveData<>();
    private final MutableLiveData<String> dateLocationText = new MutableLiveData<>("");
    private String eventId;

    @Inject
    public EventDetailViewModel(GetEventUseCase getEventUseCase) {
        this.getEventUseCase = getEventUseCase;
    }

    public LiveData<ViewState<Event>> getEventState() {
        return eventState;
    }

    public LiveData<String> getDateLocationText() {
        return dateLocationText;
    }

    public void init(String eventId) {
        if (this.eventId != null) return;
        this.eventId = eventId;
        loadEvent();
    }

    private void loadEvent() {
        if (eventId == null) return;
        eventState.setValue(ViewState.loading());

        addDisposable(
                getEventUseCase.execute(eventId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                event -> {
                                    dateLocationText.setValue(formatDateLocation(event.getDateMillis(), event.getLocation()));
                                    eventState.setValue(ViewState.success(event));
                                },
                                error -> eventState.setValue(ViewState.error(
                                        error.getMessage() != null ? error.getMessage() : "Unknown error"))
                        )
        );
    }

    private static String formatDateLocation(long dateMillis, String location) {
        String datePart = Instant.ofEpochMilli(dateMillis)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("MMM d", Locale.US));
        return (location != null && !location.isEmpty()) ? datePart + " • " + location : datePart;
    }
}
