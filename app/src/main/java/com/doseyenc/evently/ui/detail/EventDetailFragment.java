package com.doseyenc.evently.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.doseyenc.evently.databinding.FragmentEventDetailBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EventDetailFragment extends Fragment {

    private FragmentEventDetailBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String eventId = EventDetailFragmentArgs.fromBundle(requireArguments()).getEventId();
        // TODO : ViewPager + tablar gelecek
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
