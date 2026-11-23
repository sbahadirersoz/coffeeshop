package com.portfolioproject.authservice.domain.valueobjects.user.credentials;

import com.portfolioproject.authservice.domain.errors.AuthErrors;
import com.portfolioproject.commonshared.Abstracts.ResultPattern.Result;
import com.portfolioproject.authservice.domain.constraints.UsernameConstraints;
import com.portfolioproject.commonshared.Guards.PrimitiveGuard;

import java.util.Locale;
import java.util.Objects;

public final class Username
{
    private String  value;

    private Username(String value) {


        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Username username = (Username) o;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public String getValue() {
        return value;
    }

    public static Result<Username>  create(String  value)
    {
        if (value !=null)
        {
            value =value.toLowerCase(Locale.ROOT);
            value =value.trim();
        }
        Result<Void> combine = PrimitiveGuard.combine
                (
                        PrimitiveGuard.againstNullOrEmpty(value, AuthErrors.USERNAME_IS_EMPTY),
                        PrimitiveGuard.againstLengthLessThan(value, UsernameConstraints.MinValue, AuthErrors.USERNAME_LEN_UNDER_MIN_VALUE),
                        PrimitiveGuard.againstLengthGreaterThan(value, UsernameConstraints.MaxValue, AuthErrors.USERNAME_LEN_OVER_MAX_VALUE)
                );
        if (combine.isFailure())
        {
            return Result.failure(combine.getError());
        }
        return Result.success(new Username(value));

    }




}
