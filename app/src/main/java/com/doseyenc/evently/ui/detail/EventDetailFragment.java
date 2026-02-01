package com.doseyenc.evently.ui.detail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.doseyenc.evently.R;
import com.doseyenc.evently.databinding.FragmentEventDetailBinding;
import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.ui.base.ViewState;
import com.doseyenc.evently.util.Constants;
import com.google.android.material.tabs.TabLayoutMediator;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EventDetailFragment extends Fragment {

    private static final int[] TAB_TITLE_RES_IDS = {
            R.string.tab_participants,
            R.string.tab_comments,
            R.string.tab_live_status
    };

    private static final int EVENT_INFO_ANIMATION_DURATION_MS = 300;

    private FragmentEventDetailBinding binding;
    private EventDetailViewModel viewModel;
    private int imageHeightPx;
    private ValueAnimator imageAnimator;
    private ValueAnimator eventInfoAnimator;
    private int eventInfoHeaderExpandedHeight = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageHeightPx = (int) (Constants.Animation.IMAGE_HEIGHT_DP * getResources().getDisplayMetrics().density);

        String eventId = EventDetailFragmentArgs.fromBundle(requireArguments()).getEventId();
        viewModel = new ViewModelProvider(this).get(EventDetailViewModel.class);
        viewModel.init(eventId);

        setupToolbar();
        setupViewPager();
        setupTabImageAndMenu();
        observeEventState();
        observeDateLocation();
    }

    private void setupToolbar() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar, (toolbar, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            toolbar.setPadding(toolbar.getPaddingLeft(), top, toolbar.getPaddingRight(), toolbar.getPaddingBottom());
            TypedValue tv = new TypedValue();
            if (requireContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                int actionBarSize = getResources().getDimensionPixelSize(tv.resourceId);
                ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
                lp.height = actionBarSize + top;
                toolbar.setLayoutParams(lp);
            }
            return insets;
        });
        binding.toolbar.requestApplyInsets();
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(
                    ContextCompat.getColor(requireContext(), R.color.white));
            WindowCompat.getInsetsController(getActivity().getWindow(), getActivity().getWindow().getDecorView())
                    .setAppearanceLightStatusBars(true);
        }

        binding.toolbar.setTitle("");
        View titleView = LayoutInflater.from(requireContext()).inflate(R.layout.toolbar_title_centered, binding.toolbar, false);
        Toolbar.LayoutParams titleLp = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        titleLp.gravity = Gravity.CENTER;
        binding.toolbar.addView(titleView, 0, titleLp);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp());
        binding.toolbar.inflateMenu(R.menu.menu_event_detail);
        binding.toolbar.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
    }

    private boolean onToolbarMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            Toast.makeText(requireContext(), R.string.share, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_menu) {
            Toast.makeText(requireContext(), R.string.more, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void updateToolbarMenuForTab(int position) {
        if (binding == null) return;
        MenuItem shareItem = binding.toolbar.getMenu().findItem(R.id.action_share);
        MenuItem menuItem = binding.toolbar.getMenu().findItem(R.id.action_menu);
        if (shareItem != null) shareItem.setVisible(position == 0);
        if (menuItem != null) menuItem.setVisible(position != 0);
    }

    private void setupViewPager() {
        binding.viewPager.setAdapter(new DetailPagerAdapter(this));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(getString(TAB_TITLE_RES_IDS[position]))).attach();
    }

    private void setupTabImageAndMenu() {
        updateToolbarMenuForTab(0);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateToolbarMenuForTab(position);
                final boolean showImage = (position == 0);
                if (showImage) {
                    animateImageVisibility(true, () -> showEventInfoHeader());
                } else {
                    binding.eventInfoHeader.setVisibility(View.GONE);
                    animateImageVisibility(false, null);
                }
                binding.eventInfoStandalone.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void animateImageVisibility(boolean show, @Nullable Runnable onShowComplete) {
        if (imageAnimator != null) {
            imageAnimator.cancel();
            imageAnimator = null;
        }

        View container = binding.headerImageContainer;
        ViewGroup.LayoutParams lp = container.getLayoutParams();
        int startHeight = container.getHeight();
        int endHeight = show ? imageHeightPx : 0;

        if (show) container.setVisibility(View.VISIBLE);

        imageAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        imageAnimator.setDuration(Constants.Animation.IMAGE_ANIMATION_DURATION_MS);
        imageAnimator.addUpdateListener(animation -> {
            lp.height = (int) animation.getAnimatedValue();
            container.setLayoutParams(lp);
        });
        imageAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) container.setVisibility(View.GONE);
                imageAnimator = null;
                if (show && onShowComplete != null) {
                    container.post(onShowComplete);
                }
            }
        });
        imageAnimator.start();
    }

    private void showEventInfoHeader() {
        View header = binding.eventInfoHeader;
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        header.setLayoutParams(lp);
        header.setVisibility(View.VISIBLE);
        header.setAlpha(1f);
    }

    private void animateEventInfoVisibility(boolean show) {
        if (eventInfoAnimator != null) {
            eventInfoAnimator.cancel();
            eventInfoAnimator = null;
        }

        View header = binding.eventInfoHeader;
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        int startHeight = header.getHeight();
        int endHeight = show ? (Math.max(eventInfoHeaderExpandedHeight, 0)) : 0;

        if (show) {
            if (eventInfoHeaderExpandedHeight <= 0) {
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                header.setLayoutParams(lp);
                header.setVisibility(View.VISIBLE);
                header.getViewTreeObserver().addOnPreDrawListener(new android.view.ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        header.getViewTreeObserver().removeOnPreDrawListener(this);
                        int measuredHeight = header.getHeight();
                        if (measuredHeight > 0) {
                            eventInfoHeaderExpandedHeight = measuredHeight;
                        }
                        lp.height = 0;
                        header.setLayoutParams(lp);
                        header.setAlpha(0f);
                        runEventInfoShowAnimation();
                        return false;
                    }
                });
                return;
            }
            header.setVisibility(View.VISIBLE);
            startHeight = 0;
            header.setAlpha(0f);
        } else {
            if (startHeight > 0) eventInfoHeaderExpandedHeight = startHeight;
        }

        eventInfoAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        eventInfoAnimator.setDuration(EVENT_INFO_ANIMATION_DURATION_MS);
        final int startH = startHeight;
        eventInfoAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            lp.height = value;
            header.setLayoutParams(lp);
            if (show && eventInfoHeaderExpandedHeight > 0) {
                header.setAlpha((float) value / eventInfoHeaderExpandedHeight);
            } else if (!show && startH > 0) {
                header.setAlpha((float) value / startH);
            }
        });
        eventInfoAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) header.setVisibility(View.GONE);
                header.setAlpha(1f);
                eventInfoAnimator = null;
            }
        });
        eventInfoAnimator.start();
    }

    private void runEventInfoShowAnimation() {
        if (eventInfoAnimator != null) {
            eventInfoAnimator.cancel();
            eventInfoAnimator = null;
        }
        View header = binding.eventInfoHeader;
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        int endHeight = eventInfoHeaderExpandedHeight;

        if (endHeight <= 0) {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            header.setLayoutParams(lp);
            header.setAlpha(1f);
            header.setVisibility(View.VISIBLE);
            return;
        }

        eventInfoAnimator = ValueAnimator.ofInt(0, endHeight);
        eventInfoAnimator.setDuration(EVENT_INFO_ANIMATION_DURATION_MS);
        eventInfoAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            lp.height = value;
            header.setLayoutParams(lp);
            header.setAlpha((float) value / endHeight);
        });
        eventInfoAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                header.setAlpha(1f);
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                header.setLayoutParams(lp);
                eventInfoAnimator = null;
            }
        });
        eventInfoAnimator.start();
    }

    private void observeEventState() {
        viewModel.getEventState().observe(getViewLifecycleOwner(), this::renderEventState);
    }

    private void renderEventState(ViewState<Event> state) {
        if (state == null) return;

        binding.progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
        if (state.isLoading()) return;

        if (state.getErrorMessage() == null) {
            Event event = state.getData();
            if (event != null) {
                binding.setEvent(event);
                loadEventImage(event.getImageUrl());
            }
        }
    }

    private void loadEventImage(@Nullable String imageUrl) {
        int resId = R.drawable.ic_event_placeholder;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            int id = requireContext().getResources()
                    .getIdentifier(imageUrl, "drawable", requireContext().getPackageName());
            if (id != 0) resId = id;
        }
        Glide.with(requireContext())
                .load(resId)
                .placeholder(R.drawable.ic_event_placeholder)
                .error(R.drawable.ic_event_placeholder)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageEvent);
    }

    private void observeDateLocation() {
        viewModel.getDateLocationText().observe(getViewLifecycleOwner(), text -> {
            if (text != null) binding.setDateLocationText(text);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().getWindow().setStatusBarColor(
                    ContextCompat.getColor(requireContext(), R.color.primary));
            WindowCompat.getInsetsController(getActivity().getWindow(), getActivity().getWindow().getDecorView())
                    .setAppearanceLightStatusBars(false);
        }
        binding = null;
    }
}
