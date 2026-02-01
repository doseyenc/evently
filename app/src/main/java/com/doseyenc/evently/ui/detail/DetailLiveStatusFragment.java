package com.doseyenc.evently.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.doseyenc.evently.R;
import com.doseyenc.evently.databinding.FragmentDetailLiveStatusBinding;

public class DetailLiveStatusFragment extends Fragment {

    private FragmentDetailLiveStatusBinding binding;
    private EventDetailViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailLiveStatusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(EventDetailViewModel.class);
        binding.setViewModel(viewModel);

        observeLiveStatus();
        observeCountdownSeconds();
    }

    private void observeLiveStatus() {
        viewModel.getLiveStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null && binding != null) {
                binding.textLiveStatus.setText(status);
            }
        });
    }

    private void observeCountdownSeconds() {
        viewModel.getCountdownSeconds().observe(getViewLifecycleOwner(), seconds -> {
            if (binding != null) {
                binding.textNextUpdate.setText(getString(R.string.next_update_in, seconds + "s"));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refreshLiveStatus();
            viewModel.startCountdown();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewModel != null) {
            viewModel.stopCountdown();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
    }
}
