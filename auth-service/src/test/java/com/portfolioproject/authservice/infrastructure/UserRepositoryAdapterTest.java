package com.portfolioproject.authservice.infrastructure;

import com.portfolioproject.authservice.TestcontainersConfig;
import com.portfolioproject.authservice.domain.aggregates.UserAggregate;
import com.portfolioproject.authservice.domain.repositories.SecurityUserRepository;
import com.portfolioproject.authservice.domain.valueobjects.user.UserStatus;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Password;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.RoleType;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.authservice.infrastructure.adapters.SecurityUserRepositoryAdapter;
import com.portfolioproject.authservice.infrastructure.mapper.UserMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

// Real Postgres (Testcontainers) + Flyway schema + ddl-auto: validate.
// @DataJpaTest rolls back after each test, so every test starts with an empty table.
@DataJpaTest
@Import({TestcontainersConfig.class, SecurityUserRepositoryAdapter.class, UserMapper.class})
class UserRepositoryAdapterTest {

    private static final Password HASH = Password.fromHash("$2a$10$abcdefghijklmnopqrstuv");

    @Autowired
    private SecurityUserRepository repository;

    @Autowired
    private EntityManager em;

    private static Username name(String value) {
        return Username.create(value).getValue();
    }

    // flush = write SQL now, clear = forget cached entities, so the next read really hits the database
    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void save_thenFindByUsername_returnsSameRolesAndStatus() {
        UserAggregate waiter = UserAggregate.create(name("waiter01"), HASH, RoleType.WAITER);
        repository.save(waiter);
        flushAndClear();

        UserAggregate found = repository.findByUsername(name("waiter01")).orElseThrow();

        assertThat(found.getId().getUserID()).isEqualTo(waiter.getId().getUserID());
        assertThat(found.getUsername().getValue()).isEqualTo("waiter01");
        assertThat(found.getPassword()).isEqualTo(HASH);
        assertThat(found.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(found.getRoles()).containsExactly(RoleType.WAITER);
    }

    @Test
    void existsByUsername_trueAfterSave_falseForOther() {
        repository.save(UserAggregate.create(name("waiter01"), HASH, RoleType.WAITER));
        flushAndClear();

        assertThat(repository.existsByUsername(name("waiter01"))).isTrue();
        assertThat(repository.existsByUsername(name("waiter02"))).isFalse();
    }

    @Test
    void existsByRole_falseOnEmptyDb_trueAfterAdminSaved() {
        assertThat(repository.existsByRole(RoleType.ADMIN)).isFalse();

        repository.save(UserAggregate.create(name("admin001"), HASH, RoleType.ADMIN));
        flushAndClear();

        assertThat(repository.existsByRole(RoleType.ADMIN)).isTrue();
        assertThat(repository.existsByRole(RoleType.KITCHEN_STAFF)).isFalse();
    }
}
