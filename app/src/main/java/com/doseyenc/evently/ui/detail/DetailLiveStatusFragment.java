package com.doseyenc.evently.ui.detail;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.doseyenc.evently.R;
import com.doseyenc.evently.databinding.FragmentDetailLiveStatusBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailLiveStatusFragment extends Fragment {

    private static final long RIPPLE_DURATION_MS = 1800L;
    private static final long RIPPLE_STAGGER_MS = 600L;

    private FragmentDetailLiveStatusBinding binding;
    private DetailLiveStatusViewModel viewModel;
    private AnimatorSet waveAnimatorSet;

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
        String eventId = EventDetailFragmentArgs.fromBundle(requireParentFragment().requireArguments()).getEventId();
        viewModel = new ViewModelProvider(this).get(DetailLiveStatusViewModel.class);
        viewModel.init(eventId);
        binding.setViewModel(viewModel);

        observeLiveStatus();
        observeCountdownSeconds();
        setupWaveAnimation();
    }

    private void setupWaveAnimation() {
        if (binding == null) return;
        View[] ripples = { binding.ripple1, binding.ripple2, binding.ripple3 };
        AnimatorSet set = new AnimatorSet();
        Animator[] animators = new Animator[ripples.length];
        for (int i = 0; i < ripples.length; i++) {
            animators[i] = createRippleAnimator(ripples[i], i * RIPPLE_STAGGER_MS);
        }
        set.playTogether(animators);
        waveAnimatorSet = set;
    }

    private Animator createRippleAnimator(View ring, long startDelay) {
        ring.setScaleX(0.5f);
        ring.setScaleY(0.5f);
        ring.setAlpha(0.7f);
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ring, View.SCALE_X, 0.5f, 2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ring, View.SCALE_Y, 0.5f, 2f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(ring, View.ALPHA, 0.7f, 0f);

        for (ObjectAnimator a : new ObjectAnimator[] { scaleX, scaleY, alpha }) {
            a.setDuration(RIPPLE_DURATION_MS);
            a.setInterpolator(interpolator);
            a.setRepeatCount(ObjectAnimator.INFINITE);
            a.setRepeatMode(ObjectAnimator.RESTART);
            a.setStartDelay(startDelay);
        }

        AnimatorSet ringSet = new AnimatorSet();
        ringSet.playTogether(scaleX, scaleY, alpha);
        return ringSet;
    }

    private void startWaveAnimation() {
        if (waveAnimatorSet != null && binding != null) {
            waveAnimatorSet.start();
        }
    }

    private void stopWaveAnimation() {
        if (waveAnimatorSet != null && waveAnimatorSet.isRunning()) {
            waveAnimatorSet.cancel();
        }
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
        startWaveAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopWaveAnimation();
        if (viewModel != null) {
            viewModel.stopCountdown();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopWaveAnimation();
        if (viewModel != null) {
            viewModel.stopCountdown();
        }
        waveAnimatorSet = null;
        binding = null;
        viewModel = null;
    }
}
