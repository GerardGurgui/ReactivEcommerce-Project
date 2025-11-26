package Ecommerce.Reactive.UserAuthentication_service.domain.usecase;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.LoginRequestDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.*;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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
                .flatMap(userPswd -> {
                    if (!passwordEncoder.matches(loginRequestDto.getPassword(), userPswd.getPassword())) {
                        return Mono.error(new BadCredentialsException("Password does not match"));
                    }
                    return Mono.just(userPswd);
                })
                // 4 - Validate account status
                .flatMap(user -> validateAccountStatus(user))
                // 5 - Generate JWT token
                .flatMap(user -> {
                    String token = jwtProvider.generateToken(user);
                    TokenDto tokenDto = new TokenDto(token);
                    return Mono.just(tokenDto);
                })
                .doOnNext(token -> logger.info("---> Token created successfully"));

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

    private Mono<UserLoginDto> validateAccountStatus(UserLoginDto userLoginDto) {

        if (!userLoginDto.isAccountNonLocked()) {
            return Mono.error(new UserAccountLockedException("User account is locked"));
        }
        if (!userLoginDto.isEnabled()) {
            return Mono.error(new UserAccountNotEnabledException("User account is disabled"));
        }
        if (!userLoginDto.isAccountNonExpired()) {
            return Mono.error(new UserAccountIsExpiredException("User account is expired"));
        }
        return Mono.just(userLoginDto);
    }


    private Mono<UserLoginDto> userGatewayExistsValidation(String userNameOrEmail){

        if (userNameOrEmail.contains("@")) {
            return userMngConnector.getUserByEmail(userNameOrEmail);
        } else {
            return userMngConnector.getUserByUserName(userNameOrEmail);
        }
    }


}
