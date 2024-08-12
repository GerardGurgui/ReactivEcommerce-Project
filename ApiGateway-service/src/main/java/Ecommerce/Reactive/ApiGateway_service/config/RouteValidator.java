package Ecommerce.Reactive.ApiGateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("UserAuthentication-service", r -> r.path("/auth/**").uri("lb://UserAuthentication-service"))
                .route("UserManagement-service", r -> r.path("/api/usermanagement/**").uri("lb://UserManagement-service"))
                .route("MyData-service", r -> r.path("/api/MyData/**").uri("lb://MyData-service"))
                .build();
    }

    public static final List<String> openApiEndPoints = List.of(
            "auth/login",
            "api/usermanagement/addUser",
            "api/usermanagement/get",
            "auth/validateToken"
    );

    public Predicate<ServerHttpRequest> isSecured =

            request -> openApiEndPoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
