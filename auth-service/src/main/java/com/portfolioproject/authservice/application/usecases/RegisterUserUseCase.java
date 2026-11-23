package application.usecases;

import com.portfolioproject.authservice.application.commands.RegisterUserCommand;
import com.portfolioproject.authservice.application.ports.PasswordHasher;
import com.portfolioproject.authservice.domain.repositories.SecurityUserRepository;
import com.portfolioproject.authservice.domain.valueobjects.user.credentials.Username;
import com.portfolioproject.commonshared.Abstracts.ResultPattern.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserUseCase {
    private final PasswordHasher hasher;
    private final SecurityUserRepository repository;


    public RegisterUserUseCase(PasswordHasher hasher, SecurityUserRepository repository) {
        this.hasher = hasher;
        this.repository = repository;
    }

    @Transactional
    public Result<Void> execute(RegisterUserCommand  cmd)
    {
        var usernameResult = Username.create(cmd.username());
        return usernameResult.isFailure()?  Result.failure(usernameResult.getError()):Result.success();
    }

}
