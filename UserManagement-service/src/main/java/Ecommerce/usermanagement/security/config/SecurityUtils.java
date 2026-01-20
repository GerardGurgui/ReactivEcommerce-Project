package Ecommerce.usermanagement.security.config;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SecurityUtils {

    public Mono<String> extractUserUuidFromJwt() {

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .map(auth -> (Jwt) auth.getPrincipal())
                .map(Jwt::getSubject);
    }

}
