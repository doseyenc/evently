package com.doseyenc.evently.ui.detail;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.doseyenc.evently.R;
import com.doseyenc.evently.domain.model.Participant;
import com.doseyenc.evently.domain.usecase.GetParticipantsUseCase;
import com.doseyenc.evently.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class DetailParticipantsViewModel extends BaseViewModel {

    private final GetParticipantsUseCase getParticipantsUseCase;
    private final MutableLiveData<List<Participant>> participants = new MutableLiveData<>(new ArrayList<>());
    private final MediatorLiveData<List<Participant>> filteredParticipants = new MediatorLiveData<>();
    private final MutableLiveData<Integer> participantsEmptyMessageResId = new MutableLiveData<>(0);

    public final ObservableField<String> participantSearchQuery = new ObservableField<>("");
    private List<Participant> fullParticipantList = new ArrayList<>();
    private String eventId;

    @Inject
    public DetailParticipantsViewModel(GetParticipantsUseCase getParticipantsUseCase) {
        this.getParticipantsUseCase = getParticipantsUseCase;
        filteredParticipants.addSource(participants, list -> {
            fullParticipantList = list != null ? list : new ArrayList<>();
            applyFilter();
        });
        participantSearchQuery.addOnPropertyChangedCallback(new androidx.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(androidx.databinding.Observable sender, int propertyId) {
                applyFilter();
            }
        });
    }

    public void init(String eventId) {
        if (this.eventId != null) return;
        this.eventId = eventId;
        loadParticipants();
    }

    public LiveData<List<Participant>> getFilteredParticipants() {
        return filteredParticipants;
    }

    public LiveData<Integer> getParticipantsEmptyMessageResId() {
        return participantsEmptyMessageResId;
    }

    private void loadParticipants() {
        if (eventId == null) return;
        addDisposable(
                getParticipantsUseCase.execute(eventId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                list -> participants.setValue(list != null ? list : new ArrayList<>()),
                                error -> participants.setValue(new ArrayList<>())
                        )
        );
    }

    private void applyFilter() {
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
}
