package com.portfolioproject.authservice.domain.errors;

import com.portfolioproject.commonshared.Abstracts.ResultPattern.ErrorDetails;

public final class AuthErrors
{
    private AuthErrors() {}


    public static final ErrorDetails PASSWORD_NOT_VALID = new ErrorDetails("GivenPasswordIsNotValid","Auth.Password.Invalid");
    public static final ErrorDetails PASSWORD_LEN_UNDER_MIN_VALUE = new ErrorDetails("PASSWORD_LEN_UNDER_MIN_VALUE","Auth.Password.Invalid");
    public static final ErrorDetails PASSWORD_LEN_OVER_MAX_VALUE = new ErrorDetails("PASSWORD_LEN_OVER_MAX_VALUE","Auth.Password.Invalid");
    public static final ErrorDetails PASSWORD_IS_EMPTY = new ErrorDetails("GivenPasswordEmpty","Auth.Password.Invalid");
    public static final ErrorDetails USERNAME_LEN_UNDER_MIN_VALUE = new ErrorDetails("USERNAME_LEN_UNDER_MIN_VALUE","Auth.Username.Invalid");
    public static final ErrorDetails USERNAME_LEN_OVER_MAX_VALUE = new ErrorDetails("USERNAME_LEN_OVER_MAX_VALUE","Auth.Username.Invalid");
    public static final ErrorDetails USERNAME_IS_EMPTY = new ErrorDetails("GivenUsernameEmpty","Auth.Username.Invalid");
    public static final ErrorDetails USERNAME_IS_ALREADY_EXIST = new ErrorDetails("GivenUsernameAlreadyExists","Auth.Username.Exists");
    public static final ErrorDetails ROLE_IS_REQUIRED = new ErrorDetails("UserRoleRequired","Auth.Role.Required");


    public static final ErrorDetails BAD_PASSWORD_CREDENTIALS = new ErrorDetails("GivenPasswordDidntMatchWithUsername","Auth.Password.DidntMatch");

}
