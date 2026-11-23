package com.portfolioproject.authservice.domain.valueobjects.user;

import com.portfolioproject.commonshared.Abstracts.AggregateID;

import java.util.UUID;

public final class UserID extends AggregateID
{
    private UUID value;

    private UserID(UUID value) {
        this.value = value;
    }

    public static UserID createFromUUID(UUID id) {
        return new UserID(id);
    }

    public static UserID create() {
        return new UserID(UUID.randomUUID());
    }
    public UUID getUserID() {
        return value;
    }

}
