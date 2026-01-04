package ReactiveEcommerce.ProductCatalog_service.security;

import ReactiveEcommerce.ProductCatalog_service.security.validators.AudienceValidator;
import ReactiveEcommerce.ProductCatalog_service.security.validators.IssuerValidator;
import ReactiveEcommerce.ProductCatalog_service.security.validators.UserUuidValidator;
import io.jsonwebtoken.io.Decoders;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.logging.Logger;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final Logger LOGGER = Logger.getLogger(SecurityConfig.class.getName());

    @Value("${security.jwt.secret}")
    private String secretKey;

    private final JwtProperties jwtProperties;
    private final InternalServiceAuthFilter internalServiceAuthFilter;

    public SecurityConfig(JwtProperties jwtProperties,
                          InternalServiceAuthFilter internalServiceAuthFilter){
        this.jwtProperties = jwtProperties;
        this.internalServiceAuthFilter = internalServiceAuthFilter;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {

        // 1. Decode the secret key
        byte[] secretBytes = Decoders.BASE64URL.decode(secretKey);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, "HmacSHA256");

        // 2. Build decoder
        NimbusReactiveJwtDecoder decoder =
                NimbusReactiveJwtDecoder.withSecretKey(secretKeySpec).build();

        // 3. Default validator (checks exp, nbf, etc)
        OAuth2TokenValidator<Jwt> combinedValidators = getJwtOAuth2TokenValidator();

        // 6. Apply validators
        decoder.setJwtValidator(combinedValidators);

        LOGGER.info("PRODUCT CATALOG SERVICE - JWT Decoder configured with custom validators correctly");
        return decoder;
    }

    // Internal endpoints used by other services, requires api key authentication
    private static final String[] INTERNAL_ENDPOINTS = {
            "/internal/**"
    };

    @NotNull
    private OAuth2TokenValidator<Jwt> getJwtOAuth2TokenValidator() {

        OAuth2TokenValidator<Jwt> withIssuer = new IssuerValidator(jwtProperties.getExpectedIssuer());
        OAuth2TokenValidator<Jwt> withAudience = new AudienceValidator(jwtProperties.getExpectedAudience());
        OAuth2TokenValidator<Jwt> withSubject = new UserUuidValidator();

        // 4. Combine validators into a single validator
        return new DelegatingOAuth2TokenValidator<>(
                withIssuer,
                withAudience,
                withSubject
        );
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, ReactiveJwtDecoder reactiveJwtDecoder) {

        LOGGER.info("PRODUCT CATALOG SERVICE - Validating JWT on incoming requests");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(INTERNAL_ENDPOINTS).hasAuthority("INTERNAL_SERVICE")
                        .anyExchange().authenticated()
                )
                .addFilterAt(internalServiceAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                    jwt.jwtDecoder(reactiveJwtDecoder);
                    LOGGER.info("----> PRODUCT CATALOG SERVICE - Configured as OAuth2 Resource Server with JWT");
                }))
                .build();
    }

}
