package com.portfolioproject.authservice.application.ports;

public interface PasswordHasher
{
    String  hash(String rawPassword);
    boolean matches(String rawPassword, String hash);
}
