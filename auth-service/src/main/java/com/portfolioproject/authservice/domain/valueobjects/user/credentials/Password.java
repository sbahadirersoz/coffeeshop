package com.portfolioproject.authservice.domain.valueobjects.user.credentials;

import java.util.Objects;

public final class Password {

    private final String hashedPassword;

    private Password(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public static Password fromHash(String existingHash) {
        if (existingHash == null || existingHash.isBlank()) {
            throw new IllegalArgumentException("Şifre hash'i boş olamaz.");
        }
        return new Password(existingHash);
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Password p && p.hashedPassword.equals(hashedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashedPassword);
    }
}