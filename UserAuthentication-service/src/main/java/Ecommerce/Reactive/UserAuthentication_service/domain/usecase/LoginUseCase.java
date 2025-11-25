package Ecommerce.Reactive.UserAuthentication_service.domain.usecase;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.LoginRequestDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.BadCredentialsException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.EmptyCredentialsException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.UserNotFoundException;
import Ecommerce.Reactive.UserAuthentication_service.security.jwt.JwtProvider;
import Ecommerce.Reactive.UserAuthentication_service.service.usermanagement.UserManagementConnectorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class LoginUseCase {

    private final Logger logger = Logger.getLogger(LoginUseCase.class.getName());

    private final UserManagementConnectorService userMngConnector;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;

    public LoginUseCase(UserManagementConnectorService userMngConnector,
                        PasswordEncoder passwordEncoder,
                        JwtProvider jwtProvider) {
        this.userMngConnector =  userMngConnector;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public Mono<TokenDto> login(LoginRequestDto loginRequestDto) {

                // 1 - Validate input LoginRequestDto
        return validateUserLoginDto(loginRequestDto)
                // 2 - Check if user exists by username or email
                .flatMap(userNameOrEmail -> userGatewayExistsValidation(userNameOrEmail))
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")))
                .doOnNext(userDetails -> logger.info("---> User Details: " + userDetails))
                // 3 - Validate password
                .filter(userPswd -> passwordEncoder.matches(loginRequestDto.getPassword(), userPswd.getPassword()))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Password does not match")))
                // 4 - Generate JWT token
                .flatMap(user -> {
                    String token = jwtProvider.generateToken(user);
                    TokenDto tokenDto = new TokenDto(token);
                    return Mono.just(tokenDto);
                })
                .doOnNext(token -> logger.info("---> Token created successfully" + token));

    }


    private Mono<String> validateUserLoginDto(LoginRequestDto loginRequestDto) {

        String username = loginRequestDto.getUsername();
        String email = loginRequestDto.getEmail();

        if ((email == null || email.isBlank()) && (username == null || username.isBlank())) {
            return Mono.error(new EmptyCredentialsException("Username or Email cannot be empty"));
        }

        if (email != null && !email.isBlank()) {
            return Mono.just(email);
        }

        return Mono.just(username);
    }


    private Mono<UserLoginDto> userGatewayExistsValidation(String userNameOrEmail){

        if (userNameOrEmail.contains("@")) {
            return userMngConnector.getUserByEmail(userNameOrEmail);
        } else {
            return userMngConnector.getUserByUserName(userNameOrEmail);
        }
    }


}
