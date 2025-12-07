package Ecommerce.Reactive.UserAuthentication_service.domain.usecase;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.login.LoginRequestDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.login.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.*;
import Ecommerce.Reactive.UserAuthentication_service.kafka.events.UserLoginEvent;
import Ecommerce.Reactive.UserAuthentication_service.security.jwt.JwtProvider;
import Ecommerce.Reactive.UserAuthentication_service.service.usermanagement.UserManagementConnectorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.logging.Logger;

@Service
public class LoginUseCase {

    private final Logger logger = Logger.getLogger(LoginUseCase.class.getName());

    private final UserManagementConnectorService userMngConnector;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    //Kafka
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topics.user-login}")
    private String userLoginTopic;

    public LoginUseCase(UserManagementConnectorService userMngConnector,
                        PasswordEncoder passwordEncoder,
                        JwtProvider jwtProvider,
                        KafkaTemplate<String, Object> kafkaTemplate) {
        this.userMngConnector =  userMngConnector;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<TokenDto> login(LoginRequestDto loginRequestDto, String clientIp) {

        return validateUserLoginDto(loginRequestDto)
                .flatMap(userNameOrEmail -> userGatewayExistsValidation(userNameOrEmail))
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")))
                .doOnNext(userDetails -> logger.info("---> User Details: " + userDetails))
                .flatMap(userPswd -> {
                    if (!passwordEncoder.matches(loginRequestDto.getPassword(), userPswd.getPassword())) {
                        return Mono.error(new BadCredentialsException("Password does not match"));
                    }
                    return Mono.just(userPswd);
                })
                .flatMap(user -> validateAccountStatus(user))
                .flatMap(user -> {
                    String token = jwtProvider.generateToken(user);
                    kafkaPublishLoginEvent(user,clientIp);
                    return Mono.just(new TokenDto(token));
                })
                .doOnNext(token -> logger.info("---> Token created successfully"));
    }

    // Kafka publish login event to update lasted login timestamp
    private void kafkaPublishLoginEvent(UserLoginDto user, String clientIp) {

        UserLoginEvent loginEvent = new UserLoginEvent(user.getUuid(), Instant.now(), clientIp);

        kafkaTemplate.send(userLoginTopic,user.getUuid() ,loginEvent)
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                logger.severe("Failed to publish UserLoginEvent to Kafka for user: " + user.getUsername() + ", error: " + ex.getMessage());
                            } else {
                                logger.info("Successfully published UserLoginEvent to Kafka for user: " + user.getUsername());
                            }
                        });
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
            return userMngConnector.getUserLoginByEmail(userNameOrEmail);
        } else {
            return userMngConnector.getUserLoginByUsername(userNameOrEmail);
        }
    }




}
