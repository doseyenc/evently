package com.doseyenc.evently.ui.detail;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.doseyenc.evently.domain.model.Comment;
import com.doseyenc.evently.domain.usecase.AddCommentUseCase;
import com.doseyenc.evently.domain.usecase.GetCommentsUseCase;
import com.doseyenc.evently.domain.usecase.ToggleCommentLikeUseCase;
import com.doseyenc.evently.ui.base.BaseViewModel;
import com.doseyenc.evently.ui.base.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class DetailCommentsViewModel extends BaseViewModel implements CommentItemHandler {

    private final AddCommentUseCase addCommentUseCase;
    private final GetCommentsUseCase getCommentsUseCase;
    private final ToggleCommentLikeUseCase toggleCommentLikeUseCase;

    private final MutableLiveData<List<Comment>> comments = new MutableLiveData<>(new ArrayList<>());
    private final SingleLiveEvent<Comment> replyClickEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> commentPostedEvent = new SingleLiveEvent<>();

    public final ObservableField<String> draftCommentText = new ObservableField<>("");
    private String replyingToCommentId;
    private String eventId;

    @Inject
    public DetailCommentsViewModel(AddCommentUseCase addCommentUseCase,
                                   GetCommentsUseCase getCommentsUseCase,
                                   ToggleCommentLikeUseCase toggleCommentLikeUseCase) {
        this.addCommentUseCase = addCommentUseCase;
        this.getCommentsUseCase = getCommentsUseCase;
        this.toggleCommentLikeUseCase = toggleCommentLikeUseCase;
    }

    public void init(String eventId) {
        if (this.eventId != null) return;
        this.eventId = eventId;
        loadComments();
    }

    public LiveData<List<Comment>> getComments() {
        return comments;
    }

    public LiveData<Comment> getReplyClickEvent() {
        return replyClickEvent;
    }

    public LiveData<Boolean> getCommentPostedEvent() {
        return commentPostedEvent;
    }

    public void postComment() {
        String text = draftCommentText.get() != null ? draftCommentText.get().trim() : "";
        if (eventId == null || text.isEmpty()) return;
        String parentId = replyingToCommentId;
        addDisposable(
                addCommentUseCase.execute(eventId, text, parentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                comment -> {
                                    List<Comment> list = new ArrayList<>(comments.getValue() != null ? comments.getValue() : new ArrayList<>());
                                    list.add(0, comment);
                                    comments.setValue(list);
                                    draftCommentText.set("");
                                    replyingToCommentId = null;
                                    commentPostedEvent.setValue(true);
                                },
                                error -> Log.e("DetailCommentsViewModel", error.getMessage() != null ? error.getMessage() : "addComment failed")
                        )
        );
    }

    @Override
    public void onLikeClick(Comment comment) {
        toggleCommentLike(comment.getId());
    }

    @Override
    public void onReplyClick(Comment comment) {
        replyClickEvent.setValue(comment);
        replyingToCommentId = comment != null ? comment.getId() : null;
    }

    private void loadComments() {
        if (eventId == null) return;
        addDisposable(
                getCommentsUseCase.execute(eventId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                list -> comments.setValue(list != null ? list : new ArrayList<>()),
                                error -> comments.setValue(new ArrayList<>())
                        )
        );
    }

    public void toggleCommentLike(String commentId) {
        if (commentId == null || eventId == null) return;
        addDisposable(
                toggleCommentLikeUseCase.execute(commentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                updated -> applyCommentLikeUpdate(commentId, updated),
                                error -> {
                                    if (error != null && error.getMessage() != null) {
                                        Log.e("DetailCommentsViewModel", error.getMessage());
                                    }
                                }
                        )
        );
    }

    private void applyCommentLikeUpdate(String commentId, Comment updated) {
        List<Comment> current = comments.getValue();
        if (current == null) return;
        List<Comment> newList = new ArrayList<>(current.size());
        boolean found = false;
        for (Comment c : current) {
            if (c.getId().equals(commentId)) {
                newList.add(updated);
                found = true;
            } else {
                newList.add(c);
            }
        }
        if (found) {
            comments.setValue(newList);
        }
    }
}
