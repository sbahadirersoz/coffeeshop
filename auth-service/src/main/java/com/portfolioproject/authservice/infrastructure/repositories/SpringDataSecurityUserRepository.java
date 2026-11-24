package com.portfolioproject.authservice.infrastructure.repositories;

import com.portfolioproject.authservice.domain.valueobjects.user.credentials.RoleType;
import com.portfolioproject.authservice.infrastructure.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataSecurityUserRepository extends JpaRepository<UserJpaEntity, UUID>
{
    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByRolesContaining(RoleType role);
    boolean existsByUsername(String username);
}
