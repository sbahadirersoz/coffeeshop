package com.portfolioproject.authservice.infrastructure.adapters;

import com.portfolioproject.authservice.domain.aggregates.UserAggregate;
import com.portfolioproject.authservice.domain.repositories.SecurityUserRepository;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.authservice.domain.valueobjects.user.UserID;
import com.portfolioproject.authservice.infrastructure.mapper.UserMapper;
import com.portfolioproject.authservice.infrastructure.repositories.SpringDataSecurityUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SecurityUserRepositoryAdapter implements SecurityUserRepository {

    private final SpringDataSecurityUserRepository dataRepo;
    private final UserMapper userMapper;

    public SecurityUserRepositoryAdapter(SpringDataSecurityUserRepository dataRepo,
                                         UserMapper userMapper) {
        this.dataRepo = dataRepo;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<UserAggregate> findByUsername(Username username) {
        return dataRepo.findByUsername(username.getValue())
                .map(userMapper::toDomain);
    }


    @Override
    public Optional<UserAggregate> findByID(UserID id) {
        return dataRepo.findById(id.getUserID()).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(Username username)
    {
        return dataRepo.existsByUsername(username.getValue());
    }

    @Override
    public void save(UserAggregate aggregate)
    {
        dataRepo.save(userMapper.toEntity(aggregate) );
    }
}
