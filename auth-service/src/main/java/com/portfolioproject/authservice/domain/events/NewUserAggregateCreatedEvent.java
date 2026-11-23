package com.portfolioproject.authservice.domain.events;

import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.authservice.domain.valueobjects.user.UserID;
import com.portfolioproject.commonshared.Events.IDomainEvent;

public final class NewUserAggregateCreatedEvent implements IDomainEvent
{
    private UserID id;
    private Username name;



    private NewUserAggregateCreatedEvent(UserID id, Username name) {
        this.id = id;
        this.name = name;
    }


    public static NewUserAggregateCreatedEvent create(UserID id, Username name)
    {
        return new NewUserAggregateCreatedEvent(id, name);
    }

}
