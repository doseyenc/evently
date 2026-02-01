package com.doseyenc.evently.ui.detail;

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
import com.doseyenc.evently.databinding.ItemParticipantBinding;
import com.doseyenc.evently.domain.model.Participant;

import java.util.Objects;

public class ParticipantsAdapter extends ListAdapter<Participant, ParticipantsAdapter.ParticipantViewHolder> {

    private static final DiffUtil.ItemCallback<Participant> DIFF = new DiffUtil.ItemCallback<Participant>() {
        @Override
        public boolean areItemsTheSame(@NonNull Participant a, @NonNull Participant b) {
            return a.getId().equals(b.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Participant a, @NonNull Participant b) {
            return a.getId().equals(b.getId()) && a.getName().equals(b.getName())
                    && a.getRole().equals(b.getRole())
                    && Objects.equals(a.getImageUrl(), b.getImageUrl());
        }
    };

    public ParticipantsAdapter() {
        super(DIFF);
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemParticipantBinding binding = ItemParticipantBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ParticipantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static final class ParticipantViewHolder extends RecyclerView.ViewHolder {
        private final ItemParticipantBinding binding;

        ParticipantViewHolder(ItemParticipantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Participant p) {
            binding.setParticipant(p);
            loadParticipantImage(p.getImageUrl());
            binding.executePendingBindings();
        }

        private void loadParticipantImage(@Nullable String imageUrl) {
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
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.imageParticipant);
        }
    }
}
