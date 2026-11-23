package com.portfolioproject.authservice.infrastructure.config;

import com.portfolioproject.authservice.domain.repositories.SecurityUserRepository;
import com.portfolioproject.authservice.domain.service.UsernameAvailabilityPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfigurations
{

    @Bean
    UsernameAvailabilityPolicy usernameAvailabilityPolicy   (SecurityUserRepository repo)
    {
        return new UsernameAvailabilityPolicy(repo);
    }
}
