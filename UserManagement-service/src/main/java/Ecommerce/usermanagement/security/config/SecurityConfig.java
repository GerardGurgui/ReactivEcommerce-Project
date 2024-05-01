package Ecommerce.usermanagement.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


        //deprecated
        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

            return http
                    .csrf().disable()
                    .authorizeExchange()
                    .pathMatchers("/api/usermanagement/**").permitAll()
                    .anyExchange().permitAll()
                    .and().build();
        }

}
