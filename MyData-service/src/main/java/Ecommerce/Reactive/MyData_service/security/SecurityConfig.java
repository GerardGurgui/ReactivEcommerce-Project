package Ecommerce.Reactive.MyData_service.security;

import Ecommerce.Reactive.MyData_service.jwtFilterDebug.SecurityLoggingFilter;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.logging.Logger;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    //integrar 2 perfiles (dev y prod)??
    //pruebas basicas sin seguridad por ahora

    private final Logger LOGGER = Logger.getLogger(SecurityConfig.class.getName());

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        byte[] secretBytes = Decoders.BASE64URL.decode(secret);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, "HmacSHA256");
        NimbusReactiveJwtDecoder delegateHeader = NimbusReactiveJwtDecoder.withSecretKey(secretKeySpec).build();

        return token -> delegateHeader.decode(token)
                 .doOnNext(jwt -> LOGGER.info("JWT Decoded correctly: sub=" + jwt.getSubject() + " aud=" + jwt.getAudience()))
                .doOnError(err -> LOGGER.warning("Error Decoding JWT: " + err.getMessage()));
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {

        LOGGER.info("MYDATA SERVICE - Validating JWT on incoming requests");

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/api/usermanagement/addUser").permitAll()
                        .pathMatchers("/api/usermanagement/getUserBasic").permitAll()
                        .pathMatchers("/api/usermanagement/getUserInfo").permitAll()
                        .pathMatchers("/public/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.jwt(jwt -> jwt.jwtDecoder(reactiveJwtDecoder()));
                            LOGGER.info("----> MYDATA SERVICE - Configured as OAuth2 Resource Server with JWT");
                })
                .build();
    }

}