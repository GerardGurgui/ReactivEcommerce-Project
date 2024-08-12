package Ecommerce.Reactive.UserAuthentication_service.controller;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.usecase.LoginUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class LoginController {

    private final Logger logger = Logger.getLogger(LoginController.class.getName());
    private final LoginUseCase loginUseCase;
    private final ReactiveAuthenticationManager authenticationManager;

    @Autowired
    public LoginController(LoginUseCase loginUseCase, ReactiveAuthenticationManager authenticationManager) {
        this.loginUseCase = loginUseCase;
        this.authenticationManager = authenticationManager;
    }

    //// REVISAR, LOGICA AUTENTICAION EN OTRA CLASE
    public Mono<ServerResponse> login(ServerRequest request) {

        return request.bodyToMono(UserLoginDto.class)
                .flatMap(userDto -> authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()))
                        .flatMap(auth -> {
                            if (auth.isAuthenticated()) {
                                return Mono.just(userDto);
                            } else {
                                logger.severe("Authentication failed in loginController");
                                return Mono.error(new RuntimeException("Authentication failed"));
                            }
                        }))
                .flatMap(userDto -> loginUseCase.login(userDto)
                        .flatMap(token -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(token))
                )
                .onErrorResume(e -> {
                    logger.severe("Authentication failed: " + e.getMessage());
                    return ServerResponse.status(401).bodyValue("Authentication failed");
                });
    }
}
