package com.portfolioproject.authservice.domain.errors;

import com.portfolioproject.commonshared.Abstracts.ResultPattern.ErrorDetails;

import com.portfolioproject.commonshared.Constrants.ErrorMessages.BusinessErrorMessages;

public final class AggregateErrors
{

    public static final ErrorDetails ALREADY_HAS_THE_SAME_VALUE = new ErrorDetails(BusinessErrorMessages.ALREADY_HAS_THE_SAME_VALUE,"BusinessRules.Credentials.GivenParameterAlreadyExist") ;
}
