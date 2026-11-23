package com.portfolioproject.commonshared.Abstracts.ResultPattern;

public record ErrorDetails(String desc, String code)
{
    public static final ErrorDetails NONE = new ErrorDetails("", "");
}
