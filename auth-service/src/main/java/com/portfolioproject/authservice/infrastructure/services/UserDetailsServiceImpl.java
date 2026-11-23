package com.portfolioproject.authservice.infrastructure.services;

import com.portfolioproject.authservice.infrastructure.mapper.SecurityUserMapper;
import com.portfolioproject.authservice.infrastructure.mapper.UserMapper;
import com.portfolioproject.authservice.infrastructure.repositories.SpringDataSecurityUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SpringDataSecurityUserRepository repository;
    private final UserMapper userMapper;
    private final SecurityUserMapper securityUserMapper;

    public UserDetailsServiceImpl(SpringDataSecurityUserRepository repository,
                                  UserMapper userMapper,
                                  SecurityUserMapper securityUserMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
        this.securityUserMapper = securityUserMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .map(userMapper::toDomain)
                .map(securityUserMapper::toSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));
    }
}
