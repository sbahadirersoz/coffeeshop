package com.portfolioproject.authservice.infrastructure.mapper;

import com.portfolioproject.authservice.domain.aggregates.UserAggregate;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Password;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.authservice.domain.valueobjects.user.UserID;
import com.portfolioproject.authservice.infrastructure.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserAggregate toDomain(UserJpaEntity entity) {
        return UserAggregate.reconstitute(

                UserID.createFromUUID(entity.getId()),
                Username.create(entity.getUsername()).getValue(),
                Password.fromHash(entity.getPasswordHash()),
                entity.getStatus(),
                entity.getRoles()
        );
    }
    public UserJpaEntity toEntity(UserAggregate agg) {
        return UserJpaEntity.of(agg.getId().getUserID(), agg.getUsername().getValue(), agg.getPassword().getHashedPassword(), agg.getStatus(), agg.getRoles());
    }
}
