package Ecommerce.Reactive.ApiGateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

@Component
public class RouteValidator {

    private static final Logger logger = Logger.getLogger(RouteValidator.class.getName());

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("UserAuthentication-service", r -> r.path("/auth/**").uri("lb://UserAuthentication-service"))
                .route("UserManagement-service", r -> r.path("/api/usermanagement/**").uri("lb://UserManagement-service"))
                .route("MyData-service", r -> r.path("/api/MyData/**").uri("lb://MyData-service"))
                .build();
    }

    public static final List<String> openApiEndPoints = List.of(
            "/auth/login",
            "/auth/validateToken",
            "/api/usermanagement/addUser",
            "/api/usermanagement/getUserBasic",
            "/api/usermanagement/getUserInfo",
            "/public"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();

        logger.info("║ RouteValidator - Checking: " + path);

        // Verificar si el path comienza con alguna ruta pública
        boolean isPublic = openApiEndPoints.stream()
                .anyMatch(publicPath -> path.startsWith(publicPath));

        boolean isSecured = !isPublic;

        if (isSecured) {
            logger.info("║ ✅ SECURED - JWT required");
        } else {
            logger.info("║ 🔓 PUBLIC - No JWT required");
        }

        return isSecured;
    };
}
