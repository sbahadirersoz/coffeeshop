package com.portfolioproject.authservice.application.commands.createStaff;

import com.portfolioproject.authservice.application.ports.PasswordHasher;
import com.portfolioproject.authservice.domain.aggregates.UserAggregate;
import com.portfolioproject.authservice.domain.errors.AuthErrors;
import com.portfolioproject.authservice.domain.repositories.SecurityUserRepository;
import com.portfolioproject.authservice.domain.service.RawPasswordPolicy;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Password;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.commonshared.Abstracts.ResultPattern.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateStaffUserUseCase {
    private final PasswordHasher hasher;
    private final SecurityUserRepository repository;


    public CreateStaffUserUseCase(PasswordHasher hasher, SecurityUserRepository repository) {
        this.hasher = hasher;
        this.repository = repository;
    }

    @Transactional
    public Result<CreateStaffUserCommandResponse> execute(CreateStaffUserCommand cmd)
    {

        var usernameResult = Username.create(cmd.username());
        if (usernameResult.isFailure()) return Result.failure(usernameResult.getError());

        var  roleValidation  =  cmd.type();
        if (roleValidation == null)  return Result.failure(AuthErrors.ROLE_IS_REQUIRED);

        Result<Void> rawpassValidation = RawPasswordPolicy.validate(cmd.rawPassword());
        if (rawpassValidation.isFailure()) return Result.failure(rawpassValidation.getError());

        var hash = hasher.hash(cmd.rawPassword());
        var passwordResult = Password.fromHash(hash);
        UserAggregate userAggregate = UserAggregate.create(usernameResult.getValue(), passwordResult, cmd.type());
        repository.save(userAggregate);
        return Result.success();
    }

}
