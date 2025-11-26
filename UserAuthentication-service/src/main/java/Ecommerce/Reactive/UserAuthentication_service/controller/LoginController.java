package Ecommerce.Reactive.UserAuthentication_service.controller;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.LoginRequestDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.usecase.LoginUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
@Validated
@RequestMapping("/auth")
public class LoginController {

    private final LoginUseCase loginUseCase;


    public LoginController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public Mono<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return loginUseCase.login(loginRequestDto);
    }
}
