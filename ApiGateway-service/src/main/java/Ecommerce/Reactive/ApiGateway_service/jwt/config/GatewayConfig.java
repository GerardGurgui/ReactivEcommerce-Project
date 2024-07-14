package Ecommerce.Reactive.ApiGateway_service.jwt.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("UserAuthentication-service", r -> r.path("/auth/**").uri("lb://UserAuthentication-service"))
                .route("UserManagement-service", r -> r.path("/api/usermanagement/**").uri("lb://UserManagement-service"))
                .route("MyData-service", r -> r.path("/api/mydata/**").uri("lb://MyData-service"))
                .build();
    }
}
