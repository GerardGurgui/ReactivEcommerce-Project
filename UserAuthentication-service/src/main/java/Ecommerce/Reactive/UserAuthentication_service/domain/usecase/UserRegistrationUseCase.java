package Ecommerce.Reactive.UserAuthentication_service.domain.usecase;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.RegisterRequestDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.RegistrationResponseDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.UserRegisterInternalDto;
import Ecommerce.Reactive.UserAuthentication_service.service.usermanagement.UserManagementConnectorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class UserRegistrationUseCase {

    private static final Logger logger = Logger.getLogger(UserRegistrationUseCase.class.getName());

    private final PasswordEncoder passwordEncoder;
    private final UserManagementConnectorService userMngConnector;

    public UserRegistrationUseCase (PasswordEncoder passwordEncoder,
                                   UserManagementConnectorService userMngConnector) {
        this.passwordEncoder = passwordEncoder;
        this.userMngConnector = userMngConnector;
    }

    public Mono<RegistrationResponseDto> register(RegisterRequestDto dto, String clientIp) {

        String passwordHash = passwordEncoder.encode(dto.getPassword());

        UserRegisterInternalDto internalDto = UserRegisterInternalDto.builder()
                .uuid(UUID.randomUUID().toString())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .passwordHash(passwordHash)
                .name(dto.getName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .registrationIp(clientIp)
                .registeredAt(Instant.now())
                .role("USER") // default role
                .build();

        return userMngConnector.createUser(internalDto)
                .map(responseDto -> {
                    logger.info("User registered successfully: " + responseDto.getUuid());
                    return RegistrationResponseDto.builder()
                            .message("User: " + responseDto.getUsername() + " registered successfully")
                            .uuid(responseDto.getUuid())
                            .username(dto.getUsername())
                            .email(dto.getEmail())
                            .registeredAt(Instant.now())
                            .build();
                })
                .doOnSuccess(responseOk -> logger.info("RegistrationResponseDto created: " + responseOk))
                .doOnError(resposneError -> logger.severe("Error during user registration: " + resposneError.getMessage()));
    }
}
