package com.portfolioproject.authservice.infrastructure.entity;

import com.portfolioproject.authservice.domain.valueobjects.user.credentials.RoleType;
import com.portfolioproject.authservice.domain.valueobjects.user.UserStatus;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "auth_users")
public class UserJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "auth_user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<RoleType> roles = new HashSet<>();

    @Version   // optimistic locking
    private long version;

    protected UserJpaEntity() {}

    protected UserJpaEntity(UUID id, String username, String passwordHash, UserStatus status, Set<RoleType> roles) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.status = status;
        this.roles = roles;
    }
    public static UserJpaEntity of(UUID id, String username, String passwordHash,
                                   UserStatus status, Set<RoleType> roles) {
        return new UserJpaEntity(id, username, passwordHash, status, roles);
    }


    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserStatus getStatus() {
        return status;
    }

    public UUID getId() {
        return id;
    }

    public Set<RoleType> getRoles() {
        return roles;
    }
    // getter/setter veya constructor
}