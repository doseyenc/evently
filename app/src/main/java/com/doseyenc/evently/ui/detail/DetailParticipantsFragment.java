package com.doseyenc.evently.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.doseyenc.evently.databinding.FragmentDetailParticipantsBinding;

public class DetailParticipantsFragment extends Fragment {

    private FragmentDetailParticipantsBinding binding;
    private EventDetailViewModel viewModel;
    private ParticipantsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailParticipantsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(EventDetailViewModel.class);
        binding.setViewModel(viewModel);

        adapter = new ParticipantsAdapter();
        binding.recyclerParticipants.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerParticipants.setAdapter(adapter);

        observeFilteredParticipants();
        observeParticipantsEmptyMessage();
    }

    private void observeFilteredParticipants() {
        viewModel.getFilteredParticipants().observe(getViewLifecycleOwner(), list -> {
            if (binding != null) {
                adapter.submitList(list);
            }
        });
    }

    private void observeParticipantsEmptyMessage() {
        viewModel.getParticipantsEmptyMessageResId().observe(getViewLifecycleOwner(), messageResId -> {
            if (binding == null) return;
            if (messageResId == null || messageResId == 0) {
                binding.emptyParticipants.setVisibility(View.GONE);
                binding.recyclerParticipants.setVisibility(View.VISIBLE);
            } else {
                binding.emptyParticipants.setText(messageResId);
                binding.emptyParticipants.setVisibility(View.VISIBLE);
                binding.recyclerParticipants.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
