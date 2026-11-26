package com.portfolioproject.authservice.application.commands.createStaff;

import com.portfolioproject.authservice.domain.aggregates.UserAggregate;

import java.util.Set;
import java.util.UUID;

public record CreateStaffUserCommandResponse(UUID id, String username , Set<String> roles)
{
    public static CreateStaffUserCommandResponse  from(UserAggregate agg)
    {
        return new CreateStaffUserCommandResponse(agg.getId().getUserID(),agg.getUsername().getValue(),agg.permissionNames());
    }
}
