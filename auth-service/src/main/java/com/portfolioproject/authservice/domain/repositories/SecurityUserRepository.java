package com.portfolioproject.authservice.domain.repositories;

import com.portfolioproject.authservice.domain.aggregates.UserAggregate;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.authservice.domain.valueobjects.user.UserID;

import java.util.Optional;

public interface SecurityUserRepository {
    Optional<UserAggregate> findByUsername(Username username);
    Optional<UserAggregate> findByID(UserID id);
    boolean existsByUsername(Username username);
    void save(UserAggregate aggregate);


}
