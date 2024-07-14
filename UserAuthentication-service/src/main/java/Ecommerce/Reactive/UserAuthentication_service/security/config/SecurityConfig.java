package Ecommerce.Reactive.UserAuthentication_service.security.config;

import Ecommerce.Reactive.UserAuthentication_service.security.JwtFilter;
import Ecommerce.Reactive.UserAuthentication_service.security.repository.SecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final SecurityContextRepository securityContextRepository;
    private final JwtFilter jwtFilter;

    public SecurityConfig(SecurityContextRepository securityContextRepository, JwtFilter jwtFilter) {
        this.securityContextRepository = securityContextRepository;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/login").permitAll() // Permitir acceso a endpoints de autenticación
                        .pathMatchers("/api/usermanagement/addUser").permitAll() // Permitir acceso a endpoint de creación de usuario
                        .anyExchange().authenticated() // Restringir acceso a otros endpoints a usuarios autenticados
                )
                .addFilterAfter(jwtFilter, SecurityWebFiltersOrder.FIRST) // Aplicar el filtro JWT antes que otros filtros
                .securityContextRepository(securityContextRepository) // Configurar el repositorio de contexto de seguridad
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // Deshabilitar autenticación básica
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Deshabilitar form login
                .logout(ServerHttpSecurity.LogoutSpec::disable) // Deshabilitar logout
                .build();
    }
}

