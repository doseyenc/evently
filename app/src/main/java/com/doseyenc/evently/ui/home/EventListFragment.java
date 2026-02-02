package com.doseyenc.evently.ui.home;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.doseyenc.evently.R;
import com.doseyenc.evently.databinding.FragmentEventListBinding;
import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.ui.base.ViewState;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EventListFragment extends Fragment implements EventListHandler {

    private FragmentEventListBinding binding;
    private HomeViewModel viewModel;
    private EventListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding.setHandler(this);
        applyStatusBarForDrawer();
        setupToolbar();
        setupRecyclerView();
        setupChips();
        observeViewState();
        observeOpenEventDetail();
        viewModel.loadEvents();
    }

    @Override
    public void onFabClick(View view) {
        Toast.makeText(requireContext(), R.string.fab_clicked, Toast.LENGTH_SHORT).show();
    }

    /**
     * Apply status bar color and icon appearance from theme, and set DrawerLayout's
     * status bar background so the strip above the content matches (required when
     * using fitsSystemWindows on DrawerLayout).
     */
    private void applyStatusBarForDrawer() {
        TypedValue value = new TypedValue();
        if (requireContext().getTheme().resolveAttribute(android.R.attr.statusBarColor, value, true)) {
            int color = value.data;
            requireActivity().getWindow().setStatusBarColor(color);
            binding.drawerLayout.setStatusBarBackgroundColor(color);
        }
        if (requireContext().getTheme().resolveAttribute(android.R.attr.windowLightStatusBar, value, true)
                && value.type == TypedValue.TYPE_INT_BOOLEAN) {
            WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(
                    requireActivity().getWindow(), requireActivity().getWindow().getDecorView());
            insetsController.setAppearanceLightStatusBars(value.data != 0);
        }
    }

    private void setupToolbar() {
        binding.toolbar.setTitle("");
        View titleView = LayoutInflater.from(requireContext()).inflate(R.layout.toolbar_title_events, binding.toolbar, false);
        Toolbar.LayoutParams titleLp = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        titleLp.gravity = Gravity.CENTER;
        binding.toolbar.addView(titleView, 0, titleLp);
        binding.toolbar.setNavigationOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupRecyclerView() {
        adapter = new EventListAdapter(viewModel);
        binding.recyclerEvents.setAdapter(adapter);
        int spacingPx = getResources().getDimensionPixelSize(R.dimen.spacing_medium);
        binding.recyclerEvents.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = spacingPx;
            }
        });
    }

    private void setupChips() {
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            FilterType type = filterTypeFromChipId(checkedId);
            if (type != null) {
                viewModel.setFilter(type);
            }
        });
    }

    private FilterType filterTypeFromChipId(int chipId) {
        if (chipId == R.id.chipAll) return FilterType.ALL;
        if (chipId == R.id.chipToday) return FilterType.TODAY;
        if (chipId == R.id.chipUpcoming) return FilterType.UPCOMING;
        if (chipId == R.id.chipPast) return FilterType.PAST;
        return null;
    }

    private void observeViewState() {
        viewModel.getEventsViewState().observe(getViewLifecycleOwner(), this::renderViewState);
    }

    private void renderViewState(ViewState<List<Event>> state) {
        if (state == null) return;

        binding.progressBar.setVisibility(View.GONE);
        binding.textError.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.GONE);
        binding.recyclerEvents.setVisibility(View.GONE);

        if (state.isLoading()) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else if (state.getErrorMessage() != null) {
            binding.textError.setVisibility(View.VISIBLE);
            binding.textError.setText(state.getErrorMessage());
        } else if (state.getData() != null) {
            List<Event> data = state.getData();
            adapter.submitList(data);
            if (data.isEmpty()) {
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerEvents.setVisibility(View.VISIBLE);
            }
        }
    }

    private void observeOpenEventDetail() {
        viewModel.getOpenEventDetail().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            EventListFragmentDirections.ActionEventListToEventDetail action =
                    EventListFragmentDirections.actionEventListToEventDetail(event.getId());
            Navigation.findNavController(requireView()).navigate(action);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
