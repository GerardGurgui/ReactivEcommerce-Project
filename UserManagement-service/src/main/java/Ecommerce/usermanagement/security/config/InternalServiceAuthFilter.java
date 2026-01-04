package Ecommerce.usermanagement.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.logging.Logger;

@Component
public class InternalServiceAuthFilter implements WebFilter {

    private static final Logger LOGGER = Logger.getLogger(InternalServiceAuthFilter.class.getName());
    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-API-Key";

    @Value("${internal.api-key}")
    private String internalApiKey;

    // postConstruct to validate that the internalApiKey is set properly before the filter is used
    @PostConstruct
    private void validateInternalApiKey() {

        if (internalApiKey == null || internalApiKey.isEmpty()) {
            throw new IllegalStateException("Internal API key is not configured properly.");
        }

        LOGGER.info("InternalServiceAuthFilter initialized with valid internal API key.");
    }

    // Filter method to authenticate internal service calls any request to /api/usermanagement/internal/**
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getPath().value();

        // only filter internal service calls
        if (!path.startsWith("/api/usermanagement/internal/")) {
            return chain.filter(exchange);
        }

        String apiKey = exchange.getRequest().getHeaders().getFirst(INTERNAL_API_KEY_HEADER);

        if (apiKey == null || apiKey.isEmpty()) {
            LOGGER.warning("❌ Internal API call without API key to: " + path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        if (!isValidApiKey(apiKey)) {
            LOGGER.warning("❌ Invalid internal API key for: " + path);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        LOGGER.info("✅ Valid internal service authentication for: " + path);

        // create internal auth token
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "internal-service",
                        null,
                        List.of(new SimpleGrantedAuthority("INTERNAL_SERVICE"))
                );

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private boolean isValidApiKey(String providedKey) {
        if (providedKey == null) return false;

        byte[] expected = internalApiKey.getBytes(StandardCharsets.UTF_8);
        byte[] provided = providedKey.getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(expected, provided);
    }
}