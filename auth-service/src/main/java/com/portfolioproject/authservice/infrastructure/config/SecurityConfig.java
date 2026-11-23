package com.portfolioproject.authservice.infrastructure.config;

import com.portfolioproject.authservice.infrastructure.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig
{
    private  final UserDetailsServiceImpl service;

    public SecurityConfig(UserDetailsServiceImpl service) {
        this.service = service;
    }

    @Bean
    public SecurityFilterChain chain(HttpSecurity  http)  throws Exception
    {
        http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(ses ->  ses.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth ->
                auth.requestMatchers("/api/v1/**").permitAll()
                        .anyRequest().authenticated()
        )
                .authenticationProvider(authProvider());
        return http.build();
    }

    @Bean
    public AuthenticationProvider  authProvider()
    {
        DaoAuthenticationProvider  provider  = new DaoAuthenticationProvider(service);
        provider.setPasswordEncoder(encoder());
        return provider;
    }


    @Bean
    BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


}
