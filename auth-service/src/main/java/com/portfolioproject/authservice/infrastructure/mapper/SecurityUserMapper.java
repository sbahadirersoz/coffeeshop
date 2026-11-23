package com.portfolioproject.authservice.infrastructure.mapper;

import com.portfolioproject.authservice.domain.aggregates.UserAggregate;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.RoleType;
import com.portfolioproject.authservice.domain.valueobjects.user.UserStatus;
import com.portfolioproject.authservice.infrastructure.securityUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class SecurityUserMapper
{
    public securityUser toSecurityUser(UserAggregate aggregate)
    {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (RoleType role : aggregate.getRoles()) {
            // hasRole("ADMIN") için ROLE_ öneki zorunlu
            authorities.add(new SimpleGrantedAuthority( role.name()));

            // İzinler: hasAuthority("ORDER:READ") ile kullanılır
            role.getPermissions().stream()
                    .map(p -> new SimpleGrantedAuthority(p.getPermissionInfo()))
                    .forEach(authorities::add);
        }

        return new securityUser(
                aggregate.getId().getUserID(),
                aggregate.getUsername().getValue(),
                aggregate.getPassword().getHashedPassword(),
                aggregate.getStatus() == UserStatus.ACTIVE,
                Collections.unmodifiableSet(authorities)
        );
    }
    }

