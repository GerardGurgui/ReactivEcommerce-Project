package Ecommerce.Reactive.UserAuthentication_service.controller;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.LoginRequestDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.usecase.LoginUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@RestController
@Validated
@RequestMapping("/auth")
public class LoginController {

    private final LoginUseCase loginUseCase;

    public LoginController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenDto>> login(@RequestBody LoginRequestDto loginRequestDto,
                                                ServerHttpRequest exchange) {

        String clientIp = getClientIp(exchange);

        return loginUseCase.login(loginRequestDto, clientIp)
                .map(tokenDto -> ResponseEntity.ok(tokenDto));
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
