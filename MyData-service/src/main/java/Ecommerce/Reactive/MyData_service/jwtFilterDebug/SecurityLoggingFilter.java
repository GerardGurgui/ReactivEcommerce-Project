package Ecommerce.Reactive.MyData_service.jwtFilterDebug;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class SecurityLoggingFilter implements WebFilter {

    private static final Logger logger = Logger.getLogger(SecurityLoggingFilter.class.getName());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // Log the Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null) {
            logger.info("----> Received token in MyData service: " + authHeader);
        } else {
            logger.info("----> No token found in request to MyData service");
        }

        // Log the SecurityContext
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();
                    if (authentication != null) {
                        logger.info("----> SecurityContext Authentication: " + authentication);
                    } else {
                        logger.info("----> No Authentication in SecurityContext");
                    }
                })
                .then(chain.filter(exchange));
    }
}
