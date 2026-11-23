package com.portfolioproject.commonshared.Abstracts.ResultPattern;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;


public class Result<T> {
    private final boolean isSuccess;
    private final T value;
    private final ErrorDetails error;

    public boolean isSuccess() {
        return isSuccess;
    }

    public T getValue() {
        return value;
    }

    public ErrorDetails getError() {
        return error;
    }

    protected Result(boolean isSuccess, T value, ErrorDetails error) {
        if (isSuccess && error != ErrorDetails.NONE) {
            throw new IllegalArgumentException("Shouldnt have error");
        }
        if (!isSuccess && error == ErrorDetails.NONE) {
            throw new IllegalArgumentException("Needs  a  Error");
        }
        this.isSuccess = isSuccess;
        this.value = value;
        this.error = error;
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(true, value, ErrorDetails.NONE);
    }

    public static Result<Void> success() {
        return new Result<>(true, null, ErrorDetails.NONE);
    }

    // --- Başarısız Factory Metotları ---
    public static <T> Result<T> failure(ErrorDetails error) {
        return new Result<>(false, null, Objects.requireNonNull(error));
    }

    public static <T> Result<T> failure(String code, String message) {
        return new Result<>(false, null,   new ErrorDetails(message, code));
    }

    public Result<T> onSuccess(Consumer<T> action) {
        if (isSuccess) {
            action.accept(value);
        }
        return this;
    }

    public Result<T> onFailure(Consumer<ErrorDetails> action) {
        if (isFailure()) {
            action.accept(error);
        }
        return this;
    }

    public <R> Result<R> map(Function<T, R> mapper) {
        if (isFailure()) {
            return Result.failure(this.error);
        }
        return Result.success(mapper.apply(this.value));
    }
}
