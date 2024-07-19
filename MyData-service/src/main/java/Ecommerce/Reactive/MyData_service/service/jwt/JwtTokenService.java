package Ecommerce.Reactive.MyData_service.service.jwt;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Service
public class JwtTokenService {

    private static final Logger LOGGER =  Logger.getLogger(JwtTokenService.class.getName());

    private static final String SUBJECT_CLAIM = "sub";

    public Mono<String> extractUserUuidFromToken() {

        LOGGER.info("-----> Extracting user UUID from token in JwtTokenService");

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();

                    if (authentication instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                        Object sub = jwtAuth.getTokenAttributes().get(SUBJECT_CLAIM);

                        if (sub instanceof String) {
                            LOGGER.info("-----> User UUID extracted from token: " + sub);
                            return Mono.just((String) sub);
                        } else {
                            LOGGER.severe("-----> Invalid token subject type");
                            return Mono.error(new IllegalArgumentException("Invalid token subject type"));
                        }
                    }
                    LOGGER.severe("-----> No JWT token found in security context");
                    return Mono.error(new IllegalStateException("No JWT token found in security context"));
                });
    }
}

