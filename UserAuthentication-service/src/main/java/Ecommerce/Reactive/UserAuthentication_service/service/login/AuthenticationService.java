package Ecommerce.Reactive.UserAuthentication_service.service.login;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.infrastructure.mongodb.adapter.UserMongoAdapter;
import Ecommerce.Reactive.UserAuthentication_service.security.JwtProvider;
import Ecommerce.Reactive.UserAuthentication_service.security.userdetails.UserDetailsImpl;
import Ecommerce.Reactive.UserAuthentication_service.service.usermanagement.UserManagementConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.BadCredentialsException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.EmptyCredentialsException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Service
public class AuthenticationService {

    private final Logger logger = Logger.getLogger(AuthenticationService.class.getName());

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserManagementConnectorService userMngConnector;

    @Autowired
    public AuthenticationService(JwtProvider jwtProvider,
                                 PasswordEncoder passwordEncoder,
                                 UserManagementConnectorService userMngConnector) {
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.userMngConnector = userMngConnector;
    }

    public Mono<TokenDto> authenticate(UserLoginDto userLoginDto) {

        return checkIfIsUsernameOrEmail(userLoginDto)
                .flatMap(usernameOrEmail -> fetchUser(usernameOrEmail))
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")))
                .filter(user -> passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Password does not match")))
                .flatMap(user -> generateToken(userLoginDto))
                .onErrorResume(ex -> handleLoginErrors(ex));
    }

    private Mono<String> checkIfIsUsernameOrEmail(UserLoginDto userLoginDto) {

        String username = userLoginDto.getUsername();
        String email = userLoginDto.getEmail();

        if ((username == null || username.isEmpty()) && (email == null || email.isEmpty())) {
            return Mono.error(new EmptyCredentialsException("Username or Email cannot be null or empty"));
        }

        if (email != null && !email.isEmpty() && email.contains("@")) {
            return Mono.just(email);
        }

        if (username != null && !username.isEmpty()) {
            return Mono.just(username);
        }

        return Mono.error(new EmptyCredentialsException("Invalid credentials"));
    }



    public Mono<TokenDto> generateToken(UserLoginDto userLoginDto) {

        UserDetailsImpl userDetails = new UserDetailsImpl(userLoginDto.getUsername(), userLoginDto.getEmail());
        String token = jwtProvider.generateToken(userDetails);
        return Mono.just(new TokenDto(token));
    }

    public Mono<UserLoginDto> fetchUser(String usernameorEmail) {

        return userMngConnector.getUserByUsernameOrEmail(usernameorEmail)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")))
                .doOnNext(user -> logger.info("---> User fetched: " + user));
    }


    public Mono<TokenDto> handleLoginErrors(Throwable ex) {

        if (ex instanceof UserNotFoundException) {
            logger.severe("User not found during login attempt: " + ex.getMessage());
            return Mono.error(new UserNotFoundException("Login failed: User not found"));

        } else if (ex instanceof BadCredentialsException) {
            logger.severe("Password does not match during login attempt: " + ex.getMessage());
            return Mono.error(new BadCredentialsException("Login failed: Password does not match"));

        } else if (ex instanceof EmptyCredentialsException) {
            logger.severe("Username or Email cannot be null or empty during login attempt: " + ex.getMessage());
            return Mono.error(new EmptyCredentialsException("Login failed: Username or Email cannot be null or empty"));
        }
        return Mono.error(ex);
    }
}
