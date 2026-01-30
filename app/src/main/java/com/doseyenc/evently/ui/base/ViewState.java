package com.doseyenc.evently.ui.base;

import androidx.annotation.Nullable;

public final class ViewState<T> {

    private final boolean loading;
    @Nullable
    private final T data;
    @Nullable
    private final String errorMessage;

    public ViewState(boolean loading, @Nullable T data, @Nullable String errorMessage) {
        this.loading = loading;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public boolean isLoading() {
        return loading;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public static <T> ViewState<T> loading() {
        return new ViewState<>(true, null, null);
    }

    public static <T> ViewState<T> success(T data) {
        return new ViewState<>(false, data, null);
    }

    public static <T> ViewState<T> error(String message) {
        return new ViewState<>(false, null, message);
    }
}
