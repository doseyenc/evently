package com.doseyenc.evently.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.domain.usecase.GetEventsUseCase;
import com.doseyenc.evently.ui.base.BaseViewModel;
import com.doseyenc.evently.ui.base.SingleLiveEvent;
import com.doseyenc.evently.ui.base.ViewState;
import com.doseyenc.evently.util.Constants;

import java.util.ArrayList;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class HomeViewModel extends BaseViewModel {

    private final GetEventsUseCase getEventsUseCase;

    private final MutableLiveData<ViewState<List<Event>>> eventsViewState = new MutableLiveData<>();
    private final SingleLiveEvent<Event> openEventDetail = new SingleLiveEvent<>();

    private List<Event> allEvents = new ArrayList<>();
    private FilterType filterType = FilterType.ALL;

    @Inject
    public HomeViewModel(GetEventsUseCase getEventsUseCase) {
        this.getEventsUseCase = getEventsUseCase;
    }

    public LiveData<ViewState<List<Event>>> getEventsViewState() {
        return eventsViewState;
    }

    public LiveData<Event> getOpenEventDetail() {
        return openEventDetail;
    }

    public void loadEvents() {
        eventsViewState.setValue(ViewState.loading());

        addDisposable(
                getEventsUseCase.execute()
                        .delay(Constants.Loading.DELAY_MS, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                events -> {
                                    allEvents = events != null ? events : new ArrayList<>();
                                    postFilteredEvents();
                                },
                                error -> eventsViewState.setValue(ViewState.error(
                                        error.getMessage() != null ? error.getMessage() : "Unknown error"))
                        )
        );
    }

    private void postFilteredEvents() {
        List<Event> filtered = filter(allEvents, filterType);
        eventsViewState.setValue(ViewState.success(filtered));
    }

    private List<Event> filter(List<Event> events, FilterType type) {
        if (events == null) return new ArrayList<>();
        if (type == FilterType.ALL) return new ArrayList<>(events);

        ZonedDateTime startOfTodayZ = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
        long startOfToday = startOfTodayZ.toInstant().toEpochMilli();
        long startOfTomorrow = startOfTodayZ.plusDays(1).toInstant().toEpochMilli();

        List<Event> result = new ArrayList<>();
        for (Event e : events) {
            long t = e.getDateMillis();
            switch (type) {
                case TODAY:
                    if (t >= startOfToday && t < startOfTomorrow) result.add(e);
                    break;
                case UPCOMING:
                    if (t >= startOfTomorrow) result.add(e);
                    break;
                case PAST:
                    if (t < startOfToday) result.add(e);
                    break;
                default:
                    result.add(e);
            }
        }
        return result;
    }

    public void setFilter(FilterType type) {
        filterType = type;
        postFilteredEvents();
    }

    public void onEventClick(Event event) {
        openEventDetail.setValue(event);
    }
}
