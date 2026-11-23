package com.portfolioproject.authservice.domain.service;

import com.portfolioproject.authservice.domain.errors.AuthErrors;
import com.portfolioproject.commonshared.Abstracts.ResultPattern.Result;
import com.portfolioproject.authservice.domain.constraints.PasswordConstraints;
import com.portfolioproject.commonshared.Guards.PrimitiveGuard;

public class RawPasswordPolicy
{

    private RawPasswordPolicy() {
    }

    public static Result<Void> validate(String rawPassword) {
        return PrimitiveGuard.combine(
                PrimitiveGuard.againstNullOrEmpty(rawPassword, AuthErrors.PASSWORD_IS_EMPTY),
                PrimitiveGuard.againstLengthGreaterThan(rawPassword, PasswordConstraints.MaxPassLen,
                        AuthErrors.PASSWORD_LEN_OVER_MAX_VALUE),
                PrimitiveGuard.againstLengthLessThan(rawPassword, PasswordConstraints.MinPassLen,
                        AuthErrors.PASSWORD_LEN_UNDER_MIN_VALUE)
        );
    }
}
