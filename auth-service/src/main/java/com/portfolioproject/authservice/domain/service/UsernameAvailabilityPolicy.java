package com.portfolioproject.authservice.domain.service;

import com.portfolioproject.authservice.domain.errors.AuthErrors;
import com.portfolioproject.authservice.domain.repositories.SecurityUserRepository;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.commonshared.Abstracts.ResultPattern.ErrorDetails;
import com.portfolioproject.commonshared.Abstracts.ResultPattern.Result;

public class UsernameAvailabilityPolicy {
    private final SecurityUserRepository repo;

    public UsernameAvailabilityPolicy(SecurityUserRepository repo) {
        this.repo = repo;
    }

    public Result<Void> ensureAvailable(Username username) {
        if (repo.existsByUsername(username))
        {
            return Result.failure(AuthErrors.USERNAME_IS_ALREADY_EXIST);
        }
        return Result.success();
    }
}
