package com.doseyenc.evently.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doseyenc.evently.R;
import com.doseyenc.evently.databinding.ItemEventBinding;
import com.doseyenc.evently.domain.model.Event;
import com.doseyenc.evently.util.DateTimeUtils;

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

    public static final class EventViewHolder extends RecyclerView.ViewHolder {

        private final ItemEventBinding binding;

        EventViewHolder(@NonNull ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event, HomeViewModel viewModel) {
            binding.setEvent(event);
            binding.setViewModel(viewModel);
            binding.setTimeAgo(DateTimeUtils.formatTimeAgo(event.getDateMillis()));
            binding.setDateLabel(DateTimeUtils.formatDateLabel(event.getDateMillis()));
            setEventImage(event.getImageUrl());
            binding.executePendingBindings();
        }

        private void setEventImage(@Nullable String imageUrl) {
            int resId = getEventImageResId(imageUrl);
            binding.imageEvent.setImageResource(resId);
        }

        private int getEventImageResId(@Nullable String imageUrl) {
            if (imageUrl == null || imageUrl.isEmpty()) return R.drawable.ic_event_placeholder;
            switch (imageUrl) {
                case "event_1": return R.drawable.event_1;
                case "event_2": return R.drawable.event_2;
                case "event_3": return R.drawable.event_3;
                default:
                    int id = itemView.getContext().getResources()
                            .getIdentifier(imageUrl, "drawable", itemView.getContext().getPackageName());
                    return id != 0 ? id : R.drawable.ic_event_placeholder;
            }
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