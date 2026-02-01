package com.doseyenc.evently.ui.detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.doseyenc.evently.domain.usecase.GetCurrentLiveStatusUseCase;
import com.doseyenc.evently.domain.usecase.GetLiveStatusUseCase;
import com.doseyenc.evently.ui.base.BaseViewModel;
import com.doseyenc.evently.util.Constants;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class DetailLiveStatusViewModel extends BaseViewModel {

    private final GetLiveStatusUseCase getLiveStatusUseCase;
    private final GetCurrentLiveStatusUseCase getCurrentLiveStatusUseCase;

    private final MutableLiveData<String> liveStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> countdownSeconds = new MutableLiveData<>(Constants.LiveStatus.COUNTDOWN_SECONDS);

    private String eventId;
    private Disposable countdownDisposable;

    @Inject
    public DetailLiveStatusViewModel(GetLiveStatusUseCase getLiveStatusUseCase,
                                     GetCurrentLiveStatusUseCase getCurrentLiveStatusUseCase) {
        this.getLiveStatusUseCase = getLiveStatusUseCase;
        this.getCurrentLiveStatusUseCase = getCurrentLiveStatusUseCase;
    }

    public void init(String eventId) {
        if (this.eventId != null) return;
        this.eventId = eventId;
        subscribeLiveStatus();
    }

    public LiveData<String> getLiveStatus() {
        return liveStatus;
    }

    public LiveData<Integer> getCountdownSeconds() {
        return countdownSeconds;
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
                                    startCountdown();
                                },
                                error -> {
                                    if (error != null && error.getMessage() != null) {
                                        Log.e("DetailLiveStatusViewModel", error.getMessage());
                                    }
                                }
                        )
        );
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
                            if (error != null && error.getMessage() != null) {
                                Log.e("DetailLiveStatusViewModel", Objects.requireNonNull(error.getMessage()));
                            }
                        }
                );
        addDisposable(countdownDisposable);
    }

    public void stopCountdown() {
        if (countdownDisposable != null && !countdownDisposable.isDisposed()) {
            countdownDisposable.dispose();
        }
    }

    private void subscribeLiveStatus() {
        if (eventId == null) return;
        addDisposable(
                getLiveStatusUseCase.execute(eventId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                liveStatus::setValue,
                                error -> {
                                }
                        )
        );
    }
}
