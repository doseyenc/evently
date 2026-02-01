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
import com.doseyenc.evently.databinding.FragmentDetailCommentsBinding;
import com.doseyenc.evently.domain.model.Comment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DetailCommentsFragment extends Fragment {

    private FragmentDetailCommentsBinding binding;
    private EventDetailViewModel viewModel;
    private CommentsAdapter adapter;
    private Comment replyingToComment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailCommentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(EventDetailViewModel.class);
        binding.setViewModel(viewModel);

        adapter = new CommentsAdapter(viewModel);
        binding.recyclerComments.setAdapter(adapter);

        observeReplyClick();
        observeComments();
        observeCommentPosted();
    }

    private void observeReplyClick() {
        viewModel.getReplyClickEvent().observe(getViewLifecycleOwner(), comment -> {
            if (comment != null && binding != null) {
                replyingToComment = comment;
                binding.editComment.setHint(getString(R.string.add_comment) + " (Replying to " + comment.getUserName() + ")");
                binding.editComment.requestFocus();
            }
        });
    }

    private void observeComments() {
        viewModel.getComments().observe(getViewLifecycleOwner(), list -> {
            if (binding == null) return;

            boolean empty = list == null || list.isEmpty();
            binding.emptyComments.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.recyclerComments.setVisibility(empty ? View.GONE : View.VISIBLE);
            if (!empty) {
                adapter.submitList(buildThreadedList(list));
            }
        });
    }

    private void observeCommentPosted() {
        viewModel.getCommentPostedEvent().observe(getViewLifecycleOwner(), posted -> {
            if (posted != null && posted && binding != null) {
                binding.editComment.setHint(R.string.add_comment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static List<Comment> buildThreadedList(List<Comment> flat) {
        if (flat == null) return new ArrayList<>();

        List<Comment> roots = new ArrayList<>();
        List<Comment> replies = new ArrayList<>();

        for (Comment c : flat) {
            if (c.getParentCommentId() == null) roots.add(c);
            else replies.add(c);
        }

        Comparator<Comment> byTime = Comparator.comparingLong(Comment::getTimestampMillis);
        roots.sort(byTime);
        replies.sort(byTime);
        List<Comment> result = new ArrayList<>();
        for (Comment root : roots) {
            result.add(root);
            for (Comment r : replies) {
                if (root.getId().equals(r.getParentCommentId())) result.add(r);
            }
        }

        return result;
    }
}
