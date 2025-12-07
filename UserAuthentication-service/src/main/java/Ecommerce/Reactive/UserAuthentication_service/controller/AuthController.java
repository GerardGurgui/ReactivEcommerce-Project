package Ecommerce.Reactive.UserAuthentication_service.controller;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.login.LoginRequestDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.RegisterRequestDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.RegistrationResponseDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.usecase.LoginUseCase;
import Ecommerce.Reactive.UserAuthentication_service.domain.usecase.UserRegistrationUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@RestController
@Validated
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final UserRegistrationUseCase userRegistrationUseCase;

    public AuthController(LoginUseCase loginUseCase, UserRegistrationUseCase userRegistrationUseCase) {
        this.loginUseCase = loginUseCase;
        this.userRegistrationUseCase = userRegistrationUseCase;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenDto>> login(@RequestBody LoginRequestDto loginRequestDto,
                                                ServerHttpRequest request) {
        String clientIp = getClientIp(request);

        return loginUseCase.login(loginRequestDto, clientIp)
                .map(tokenDto -> ResponseEntity.ok(tokenDto));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<RegistrationResponseDto>> registerUser(@RequestBody RegisterRequestDto registerRequestDto,
                                                                      ServerHttpRequest request) {
        String clientIp = getClientIp(request);

        return userRegistrationUseCase.register(registerRequestDto, clientIp)
                .map(responseDto -> ResponseEntity.ok(responseDto));
    }


    private String getClientIp(ServerHttpRequest request) {

        // first try to get IP from X-Forwarded-For header (if behind proxy)
        String ip = request.getHeaders().getFirst("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {

            // if no proxy, get IP from remote address
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            ip = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
        }

        // if there are multiple IPs, take the first one (which is the original client IP)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

}
