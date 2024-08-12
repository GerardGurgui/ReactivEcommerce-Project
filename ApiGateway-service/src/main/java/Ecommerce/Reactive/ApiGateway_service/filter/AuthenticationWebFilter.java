package Ecommerce.Reactive.ApiGateway_service.filter;

import Ecommerce.Reactive.ApiGateway_service.config.RouteValidator;
import Ecommerce.Reactive.ApiGateway_service.exceptions.InvalidAuthorizationHeaderException;
import Ecommerce.Reactive.ApiGateway_service.exceptions.MissingAuthorizationHeaderException;
import Ecommerce.Reactive.ApiGateway_service.exceptions.TokenExpiredException;
import Ecommerce.Reactive.ApiGateway_service.exceptions.UnauthorizedException;
import Ecommerce.Reactive.ApiGateway_service.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class AuthenticationWebFilter implements WebFilter {

    private static final Logger logger = Logger.getLogger(AuthenticationWebFilter.class.getName());

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;
    private final JwtReactiveAuthenticationManager jwtReactAuthManager;

    @Autowired
    public AuthenticationWebFilter(JwtUtil jwtUtil,
                                   RouteValidator routeValidator,
                                   JwtReactiveAuthenticationManager jwtReactAuthManager) {
        this.jwtUtil = jwtUtil;
        this.routeValidator = routeValidator;
        this.jwtReactAuthManager = jwtReactAuthManager;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // Check if the route is secured or not
        if (routeValidator.isSecured.test(exchange.getRequest())) {
            logger.info("----> Filtering request in gateway");

            Optional<String> tokenOptional = extractToken(exchange.getRequest());

            if (tokenOptional.isEmpty()) {
                return onError(exchange, HttpStatus.NO_CONTENT);
            }

            return validateTokenAndCreateBearerAuthToken(tokenOptional.get())
                    .map(authToken -> authenticateAndSetSecurityContext(exchange, chain, authToken))
                    .orElseGet(() -> onError(exchange, HttpStatus.UNAUTHORIZED));
        } else {
            logger.info("----> Request is unsecured");
            return chain.filter(exchange);
        }
    }


    private Optional<String> extractToken(ServerHttpRequest request) {

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            logger.severe("Authorization header is missing");
            throw new MissingAuthorizationHeaderException("Authorization header is missing");
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        logger.info("----> Authorization header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String jwtToken = authHeader.substring(7);
            logger.info("----> Extracted token from header: " + jwtToken);
            return Optional.of(jwtToken);

        } else {
            logger.severe("----> Invalid Authorization header");
            throw new InvalidAuthorizationHeaderException("Invalid Authorization header");
        }
    }

    private Optional<BearerTokenAuthenticationToken> validateTokenAndCreateBearerAuthToken(String jwtToken) {

        try {
            Claims claims = jwtUtil.validate(jwtToken);

            if (jwtUtil.isTokenExpired(claims)) {
                logger.severe("----> Token is expired");
                throw new TokenExpiredException("Token is expired");
            }
            logger.info("----> Token is valid");

            //create a BearerTokenAuthenticationToken
            return Optional.of(new BearerTokenAuthenticationToken(jwtToken));

        } catch (Exception e) {
            logger.severe("----> Token validation failed: " + e.getMessage());
            throw new UnauthorizedException("Token validation failed");
        }
    }

    private Mono<Void> authenticateAndSetSecurityContext(ServerWebExchange exchange,
                                                         WebFilterChain chain,
                                                         BearerTokenAuthenticationToken authToken) {

        return jwtReactAuthManager.authenticate(authToken)
                .flatMap(authentication -> {

                    if (authentication instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;

                        // Convert roles to authorities
                        Collection<? extends GrantedAuthority> authorities = convertRolesToAuthorities(jwtAuth.getToken().getClaims());

                        // Create a new JwtAuthenticationToken with authorities
                        jwtAuth = new JwtAuthenticationToken(jwtAuth.getToken(), authorities);

                        // Add claims to the exchange attributes
                        exchange.getAttributes().put("claims", jwtAuth.getToken().getClaims());
                        logger.info("----> Token authenticated successfully and claims added to the exchange: " + jwtAuth.getToken().getClaims());

                        // Set the security context
                        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                        securityContext.setAuthentication(jwtAuth);
                        logger.info("----> Security context set correctly: " + securityContext);

                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                    } else {
                        logger.severe("----> Authentication token is not of type JwtAuthenticationToken");
                        return onError(exchange, HttpStatus.BAD_REQUEST);
                    }
                })
                .doOnSuccess(aVoid -> logger.info("----> Request filtered successfully and security context set"))
                .onErrorResume(e -> {
                    logger.severe("----> Authentication manager error: " + e.getMessage());
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                });
    }




    private Collection<? extends GrantedAuthority> convertRolesToAuthorities(Map<String, Object> claims) {

        List<Map<String, String>> roles = (List<Map<String, String>>) claims.get("roles");
        return roles.stream()
                .map(roleMap -> new SimpleGrantedAuthority(roleMap.get("authority")))
                .collect(Collectors.toList());
    }



    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}


