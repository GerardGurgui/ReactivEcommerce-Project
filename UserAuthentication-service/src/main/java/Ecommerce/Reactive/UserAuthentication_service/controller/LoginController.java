package Ecommerce.Reactive.UserAuthentication_service.controller;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.usecase.LoginUseCase;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
//@RequestMapping("/api/user-authentication")
public class LoginController {

    private final Logger logger = Logger.getLogger(LoginController.class.getName());

    private final LoginUseCase loginUseCase;

    public LoginController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    public Mono<ServerResponse> login(ServerRequest request){

        return request.bodyToMono(UserLoginDto.class)
                .flatMap(userDto -> loginUseCase.login(userDto)
                        .flatMap(token -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(token)));
    }

}
