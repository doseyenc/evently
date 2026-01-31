package com.doseyenc.evently.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.doseyenc.evently.R;
import com.doseyenc.evently.databinding.ItemEventBinding;
import com.doseyenc.evently.domain.model.Event;

import java.util.Calendar;
import java.util.Locale;

public class EventListAdapter extends ListAdapter<Event, EventListAdapter.EventViewHolder> {

    private final HomeViewModel viewModel;

    public EventListAdapter(HomeViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventBinding binding = ItemEventBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = getItem(position);
        holder.bind(event, viewModel);
    }

    static String formatTimeAgo(long dateMillis) {
        long now = System.currentTimeMillis();
        long diff = now - dateMillis;
        if (diff < 60_000) return "just now";
        if (diff < 3600_000) return (diff / 60_000) + "m ago";
        if (diff < 86400_000) return (diff / 3600_000) + "h ago";
        return (diff / 86400_000) + "d ago";
    }

    static String formatDateLabel(long dateMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return (month != null ? month.toUpperCase(Locale.US) : null) + " " + day;
    }

    public static final class EventViewHolder extends RecyclerView.ViewHolder {

        private final ItemEventBinding binding;

        EventViewHolder(@NonNull ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event, HomeViewModel viewModel) {
            binding.setEvent(event);
            binding.setViewModel(viewModel);
            binding.setTimeAgo(formatTimeAgo(event.getDateMillis()));
            binding.setDateLabel(formatDateLabel(event.getDateMillis()));
            setEventImage(event.getImageUrl());
            binding.executePendingBindings();
        }

        private void setEventImage(@Nullable String imageUrl) {
            int resId = R.drawable.ic_event_placeholder;
            if (imageUrl != null && !imageUrl.isEmpty()) {
                int id = itemView.getContext().getResources()
                        .getIdentifier(imageUrl, "drawable", itemView.getContext().getPackageName());
                if (id != 0) resId = id;
            }
            Glide.with(itemView.getContext())
                    .load(resId)
                    .placeholder(R.drawable.ic_event_placeholder)
                    .error(R.drawable.ic_event_placeholder)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.imageEvent);
        }
    }

    private static final DiffUtil.ItemCallback<Event> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.equals(newItem);
        }
    };
}