package com.doseyenc.evently.ui.detail;

import com.doseyenc.evently.domain.model.Comment;

public interface CommentItemHandler {

    void onLikeClick(Comment comment);

    void onReplyClick(Comment comment);
}
