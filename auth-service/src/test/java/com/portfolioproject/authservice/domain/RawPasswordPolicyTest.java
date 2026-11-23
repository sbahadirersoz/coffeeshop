package com.portfolioproject.authservice.domain;

import com.portfolioproject.authservice.domain.errors.AuthErrors;
import com.portfolioproject.authservice.domain.service.RawPasswordPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

// Contract: a raw password must be 8-64 characters (BCrypt ignores everything after 72 bytes).
class RawPasswordPolicyTest {

    @ParameterizedTest
    @ValueSource(ints = {8, 12, 13, 64})
    void validate_8to64_succeeds(int length) {
        assertThat(RawPasswordPolicy.validate("a".repeat(length)).isSuccess()).isTrue();
    }

    @Test
    void validate_7_failsWithUnderMin() {
        assertThat(RawPasswordPolicy.validate("a".repeat(7)).getError())
                .isEqualTo(AuthErrors.PASSWORD_LEN_UNDER_MIN_VALUE);
    }

    @Test
    void validate_65_failsWithOverMax() {
        assertThat(RawPasswordPolicy.validate("a".repeat(65)).getError())
                .isEqualTo(AuthErrors.PASSWORD_LEN_OVER_MAX_VALUE);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "        "})
    void validate_nullOrBlank_failsWithEmpty(String value) {
        assertThat(RawPasswordPolicy.validate(value).getError()).isEqualTo(AuthErrors.PASSWORD_IS_EMPTY);
    }
}
