package com.doseyenc.evently.ui.detail;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.doseyenc.evently.R;
import com.doseyenc.evently.databinding.ItemCommentBinding;
import com.doseyenc.evently.domain.model.Comment;
import com.doseyenc.evently.util.DateTimeUtils;

public class CommentsAdapter extends ListAdapter<Comment, CommentsAdapter.CommentViewHolder> {

    private final EventDetailViewModel viewModel;

    public CommentsAdapter(EventDetailViewModel viewModel) {
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

        void bind(Comment c, EventDetailViewModel viewModel) {
            binding.setComment(c);
            binding.setTimeAgoText(DateTimeUtils.formatTimeAgo(c.getTimestampMillis()));
            binding.setLikeCountText(String.valueOf(c.getLikeCount()));
            binding.setHandler(viewModel);

            boolean isReply = c.getParentCommentId() != null;
            int marginStartPx = (int) (isReply ? itemView.getContext().getResources().getDimension(R.dimen.spacing_xxlarge) : 0);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            if (lp != null) {
                lp.setMarginStart(marginStartPx);
                itemView.setLayoutParams(lp);
            }

            binding.executePendingBindings();
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
                    && a.getText().equals(b.getText());
        }
    };
}
