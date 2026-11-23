package com.portfolioproject.authservice.application.ports;

public interface PasswordHasher
{
    String  hash(String rawPassword);
}
