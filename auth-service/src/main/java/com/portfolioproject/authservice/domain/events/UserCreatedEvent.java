package com.portfolioproject.authservice.domain.events;

import com.portfolioproject.authservice.domain.valueobjects.user.credentials.RoleType;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.authservice.domain.valueobjects.user.UserID;
import com.portfolioproject.commonshared.Events.IDomainEvent;

public final class UserCreatedEvent implements IDomainEvent
{
    private UserID id;
    private Username name;
    private RoleType type;



    private UserCreatedEvent(UserID id, Username name,RoleType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public UserID getId() {
        return id;
    }

    public Username getUsername() {
        return name;
    }

    public static UserCreatedEvent create(UserID id, Username name, RoleType  type)
    {
        return new UserCreatedEvent(id, name,type);
    }

}
