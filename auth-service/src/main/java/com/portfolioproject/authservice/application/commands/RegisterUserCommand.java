package com.portfolioproject.authservice.application.commands;

public record RegisterUserCommand(String username, String rawPassword) {}