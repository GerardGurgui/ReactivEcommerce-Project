package Ecommerce.Reactive.ApiGateway_service.security.config;

import Ecommerce.Reactive.ApiGateway_service.security.config.validators.AudienceValidator;
import Ecommerce.Reactive.ApiGateway_service.security.config.validators.IssuerValidator;
import Ecommerce.Reactive.ApiGateway_service.security.config.validators.UserUuidValidator;
import io.jsonwebtoken.io.Decoders;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String secretKey;

    private final JwtProperties jwtProperties;

    public SecurityConfig(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
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
        return decoder;
    }

    @NotNull
    private OAuth2TokenValidator<Jwt> getJwtOAuth2TokenValidator() {

        OAuth2TokenValidator<Jwt> withIssuer = new IssuerValidator(jwtProperties.getExpectedIssuer());
        OAuth2TokenValidator<Jwt> withAudience = new AudienceValidator(jwtProperties.getExpectedAudience());
        OAuth2TokenValidator<Jwt> withSubject = new UserUuidValidator();

        // 4. Combine validators into a single validator
        OAuth2TokenValidator<Jwt> combinedValidators =
                new DelegatingOAuth2TokenValidator<>(
                        withIssuer,
                        withAudience,
                        withSubject
                );
        return combinedValidators;
    }


    // Define the security web filter chain for handling HTTP security at api gateway level
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveJwtDecoder jwtDecoder) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/public/**").permitAll()
                        .pathMatchers("/api/usermanagement/addUser").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder)
                        )
                )
                .build();
    }


}


