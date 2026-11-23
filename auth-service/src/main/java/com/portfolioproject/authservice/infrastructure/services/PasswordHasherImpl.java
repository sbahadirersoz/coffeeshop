package com.portfolioproject.authservice.infrastructure.services;

import com.portfolioproject.authservice.application.ports.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordHasherImpl implements PasswordHasher
{

    private final PasswordEncoder  encoder;

    public PasswordHasherImpl(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String hash(String rawPassword)
    {
        return encoder.encode(rawPassword);

    }
}
