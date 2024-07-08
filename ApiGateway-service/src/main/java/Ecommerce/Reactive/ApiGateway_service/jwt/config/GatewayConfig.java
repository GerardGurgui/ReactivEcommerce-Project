package Ecommerce.Reactive.ApiGateway_service.jwt.config;

import Ecommerce.Reactive.ApiGateway_service.jwt.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("UserAuthentication-service", r -> r.path("/auth/**").filters(f -> f.filter(jwtTokenFilter.apply(new JwtTokenFilter.Config())))
                        .uri("lb://UserAuthentication-service"))
                .route("UserManagement-service", r -> r.path("/api/usermanagement/**").filters(f -> f.filter(jwtTokenFilter.apply(new JwtTokenFilter.Config())))
                        .uri("lb://UserManagement-service"))
                .route("MyData-service", r -> r.path("/api/mydata/**").filters(f -> f.filter(jwtTokenFilter.apply(new JwtTokenFilter.Config())))
                        .uri("lb://MyData-service"))
                .build();
    }
}
