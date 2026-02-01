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
import com.doseyenc.evently.databinding.ItemCommentBinding;
import com.doseyenc.evently.domain.model.Comment;
import com.doseyenc.evently.util.DateTimeUtils;
import java.util.Objects;

public class CommentsAdapter extends ListAdapter<Comment, CommentsAdapter.CommentViewHolder> {

    private final CommentItemHandler viewModel;

    public CommentsAdapter(CommentItemHandler viewModel) {
        super(DIFF);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(getItem(position), viewModel);
    }

    public static final class CommentViewHolder extends RecyclerView.ViewHolder {
        private final ItemCommentBinding binding;

        CommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Comment c, CommentItemHandler handler) {
            binding.setComment(c);
            binding.setTimeAgoText(DateTimeUtils.formatTimeAgo(c.getTimestampMillis()));
            binding.setLikeCountText(String.valueOf(c.getLikeCount()));
            binding.setHandler(handler);
            loadCommentAvatar(c.getUserImageUrl());

            boolean isReply = c.getParentCommentId() != null;
            int marginStartPx = (int) (isReply ? itemView.getContext().getResources().getDimension(R.dimen.spacing_xxlarge) : 0);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            if (lp != null) {
                lp.setMarginStart(marginStartPx);
                itemView.setLayoutParams(lp);
            }

            binding.executePendingBindings();
        }

        private void loadCommentAvatar(@Nullable String userImageUrl) {
            int resId = R.drawable.ic_event_placeholder;
            if (userImageUrl != null && !userImageUrl.isEmpty()) {
                int id = itemView.getContext().getResources()
                        .getIdentifier(userImageUrl, "drawable", itemView.getContext().getPackageName());
                if (id != 0) resId = id;
            }
            Glide.with(itemView.getContext())
                    .load(resId)
                    .placeholder(R.drawable.ic_event_placeholder)
                    .error(R.drawable.ic_event_placeholder)
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.imageCommentAvatar);
        }
    }

    private static final DiffUtil.ItemCallback<Comment> DIFF = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Comment a, @NonNull Comment b) {
            return a.getId().equals(b.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Comment a, @NonNull Comment b) {
            return a.getLikeCount() == b.getLikeCount() && a.isLikedByMe() == b.isLikedByMe()
                    && a.getText().equals(b.getText())
                    && Objects.equals(a.getUserImageUrl(), b.getUserImageUrl());
        }
    };
}
