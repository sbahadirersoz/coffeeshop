package com.portfolioproject.commonshared.Guards;

import com.portfolioproject.commonshared.Abstracts.ResultPattern.ErrorDetails;
import com.portfolioproject.commonshared.Abstracts.ResultPattern.Result;

public class PrimitiveGuard
{

    private PrimitiveGuard() {
    }
    public static Result<Void> againstNullOrEmpty(String value, ErrorDetails error) {
        if (value == null || value.trim().isEmpty()) {
            return Result.failure(error);
        }
        return Result.success();
    }

    public static Result<Void> againstNull(Object value, ErrorDetails error) {
        if (value == null) {
            return Result.failure(error);
        }
        return Result.success();
    }

    public static Result<Void> againstLengthLessThan(String value, int minLength, ErrorDetails error) {
        if (value != null && value.length() < minLength) {
            return Result.failure(error);
        }
        return Result.success();
    }

    public static Result<Void> againstLengthGreaterThan(String value, int maxLength, ErrorDetails error) {
        if (value != null && value.length() > maxLength) {
            return Result.failure(error);
        }
        return Result.success();
    }

    public static Result<Void> combine(Result<Void>... results) {
        for (Result<Void> result : results) {
            if (result.isFailure()) {
                return result;
            }
        }
        return Result.success();
    }
}
