package com.portfolioproject.authservice.application;

import com.portfolioproject.authservice.application.commands.createStaff.CreateStaffUserUseCase;
import com.portfolioproject.authservice.application.commands.createStaff.CreateStaffUserCommand;
import com.portfolioproject.authservice.application.ports.PasswordHasher;
import com.portfolioproject.authservice.domain.aggregates.UserAggregate;
import com.portfolioproject.authservice.domain.errors.AuthErrors;
import com.portfolioproject.authservice.domain.repositories.SecurityUserRepository;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.RoleType;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.commonshared.Abstracts.ResultPattern.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Contract: new CreateStaffUserUseCase(PasswordHasher, SecurityUserRepository)
//           Result<UUID> execute(CreateStaffUserCommand(username, rawPassword, role))
// Valid input -> saves one user with the HASHED password, returns its id. Any error -> saves nothing.
@ExtendWith(MockitoExtension.class)
class CreateStaffUserUseCaseTest {

    // Fake hasher: easy to see in asserts that the raw password was hashed, not stored as is.
    private static final PasswordHasher FAKE_HASHER = new PasswordHasher() {
        @Override
        public String hash(String rawPassword) {
            return "hashed:" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String hash) {
            return hash.equals("hashed:" + rawPassword);
        }
    };

    @Mock
    private SecurityUserRepository repository;

    private CreateStaffUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateStaffUserUseCase(FAKE_HASHER, repository);
    }

    private static Username name(String value) {
        return Username.create(value).getValue();
    }

    @Test
    void valid_savesUserWithHashedPassword_returnsId() {
        Result<UUID> result = useCase.execute(new CreateStaffUserCommand("waiter01", "secret123", RoleType.WAITER));

        assertThat(result.isSuccess()).isTrue();

        ArgumentCaptor<UserAggregate> saved = ArgumentCaptor.forClass(UserAggregate.class);
        verify(repository).save(saved.capture());
        UserAggregate user = saved.getValue();

        assertThat(user.getPassword().getHashedPassword()).isEqualTo("hashed:secret123");
        assertThat(user.getUsername().getValue()).isEqualTo("waiter01");
        assertThat(user.getRoles()).containsExactly(RoleType.WAITER);
        assertThat(result.getValue()).isEqualTo(user.getId().getUserID());
    }

    @Test
    void takenUsername_returnsUsernameExists_savesNothing() {
        when(repository.existsByUsername(name("waiter01"))).thenReturn(true);

        Result<UUID> result = useCase.execute(new CreateStaffUserCommand("waiter01", "secret123", RoleType.WAITER));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(AuthErrors.USERNAME_IS_ALREADY_EXIST);
        verify(repository, never()).save(any());
    }

    @Test
    void takenUsername_differentCase_returnsUsernameExists() {
        // stored as "waiter01"; " WAITER01" must be trimmed + lowercased before the check
        when(repository.existsByUsername(name("waiter01"))).thenReturn(true);

        Result<UUID> result = useCase.execute(new CreateStaffUserCommand(" WAITER01", "secret123", RoleType.WAITER));

        assertThat(result.getError()).isEqualTo(AuthErrors.USERNAME_IS_ALREADY_EXIST);
        verify(repository, never()).save(any());
    }

    @Test
    void shortPassword_returnsPasswordTooShort_savesNothing() {
        Result<UUID> result = useCase.execute(new CreateStaffUserCommand("waiter01", "1234567", RoleType.WAITER));

        assertThat(result.getError()).isEqualTo(AuthErrors.PASSWORD_LEN_UNDER_MIN_VALUE);
        verify(repository, never()).save(any());
    }

    @Test
    void nullRole_returnsRoleRequired_savesNothing() {
        Result<UUID> result = useCase.execute(new CreateStaffUserCommand("waiter01", "secret123", null));

        assertThat(result.getError()).isEqualTo(AuthErrors.ROLE_IS_REQUIRED);
        verify(repository, never()).save(any());
    }
}
