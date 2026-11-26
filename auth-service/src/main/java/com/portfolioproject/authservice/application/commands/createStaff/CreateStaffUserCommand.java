package com.portfolioproject.authservice.application.commands.createStaff;

import com.portfolioproject.authservice.domain.valueobjects.user.credentials.RoleType;

public record CreateStaffUserCommand(String username, String rawPassword, RoleType type) {}