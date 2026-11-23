package com.portfolioproject.authservice.domain;

import com.portfolioproject.authservice.domain.errors.AuthErrors;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.commonshared.Abstracts.ResultPattern.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

// Contract: a username is trimmed and lowercased, then must be 6-12 characters.
class UsernameTest {

    @Test
    void create_trimsAndLowercases() {
        Result<Username> result = Username.create("  Waiter01 ");

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getValue()).isEqualTo("waiter01");
    }

    @Test
    void sameNameDifferentCase_areEqual() {
        assertThat(Username.create("WAITER01").getValue()).isEqualTo(Username.create("waiter01").getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdef", "abcdefghijkl"}) // 6 and 12: the limits themselves are valid
    void create_atLimits_succeeds(String value) {
        assertThat(Username.create(value).isSuccess()).isTrue();
    }

    @Test
    void create_5chars_failsWithUnderMin() {
        assertThat(Username.create("abcde").getError()).isEqualTo(AuthErrors.USERNAME_LEN_UNDER_MIN_VALUE);
    }

    @Test
    void create_13chars_failsWithOverMax() {
        assertThat(Username.create("abcdefghijklm").getError()).isEqualTo(AuthErrors.USERNAME_LEN_OVER_MAX_VALUE);
    }

    @Test
    void create_spacesAroundShortName_countAfterTrim() {
        // "  abcde  " is 9 characters but only 5 after trimming
        assertThat(Username.create("  abcde  ").getError()).isEqualTo(AuthErrors.USERNAME_LEN_UNDER_MIN_VALUE);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void create_nullOrBlank_failsWithEmpty(String value) {
        assertThat(Username.create(value).getError()).isEqualTo(AuthErrors.USERNAME_IS_EMPTY);
    }
}
