package Ecommerce.Reactive.ApiGateway_service.security.config;

import Ecommerce.Reactive.ApiGateway_service.jwt.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterBefore(jwtTokenFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/public/**").permitAll()
                        .pathMatchers("/auth/login").permitAll()
                        .pathMatchers("/api/usermanagement/addUser").permitAll()
                        .anyExchange().authenticated()
                );

        //REVISAR TODAS LAS CONFIGURACIONES DE SEGURIDAD

//                .headers(headers -> headers
//                        .contentSecurityPolicy("default-src 'self'")
//                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
//                        .permissionsPolicy(permissions -> permissions.policy("geolocation=(self), camera=()"))
//                        .strictTransportSecurity()
//                        .httpStrictTransportSecurity()
//                        .includeSubDomains(true)
//                        .maxAgeInSeconds(31536000)
//                        .and()
//                        .frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::deny)
//                        .xssProtection(xss -> xss.block(true))
//                        .cacheControl(ServerHttpSecurity.HeaderSpec.CacheControlSpec::disable)
//                        .contentTypeOptions(ServerHttpSecurity.HeaderSpec.ContentTypeOptionsSpec::disable)
//                )
//                .cors(cors -> cors.configurationSource(request -> {
//                    var corsConfiguration = new CorsConfiguration();
//                    corsConfiguration.setAllowedOrigins(List.of("https://trusteddomain.com"));
//                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
//                    corsConfiguration.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
//                    return corsConfiguration;
//                }));

        return http.build();
    }
}


