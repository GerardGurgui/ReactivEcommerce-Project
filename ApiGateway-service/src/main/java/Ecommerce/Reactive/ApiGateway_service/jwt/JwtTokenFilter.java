package Ecommerce.Reactive.ApiGateway_service.jwt;


// clase encargada de extraer el token de la cabecera de la petición y validar su autenticidad
// almacenando el token en el contexto de seguridad de la aplicación

import org.springframework.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GatewayFilter;

import java.util.logging.Logger;


@Component
public class JwtTokenFilter extends AbstractGatewayFilterFactory<JwtTokenFilter.Config> {

    private static final Logger logger = Logger.getLogger(JwtTokenFilter.class.getName());

    public JwtTokenFilter() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            //excluir peticiones login
            if (exchange.getRequest().getURI().getPath().contains("/auth/login")) {
                logger.info("-----> Excluyendo peticiones login");
                return chain.filter(exchange);
            }

            // extraer el token de la cabecera de la petición
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            logger.info("-----> TODA SOLICITUD EXCEPTO LOGIN PASA POR AQUI");

            //validar que el token empiece con Bearer y almacenarlo en el contexto de seguridad de la aplicación
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwtToken = authHeader.substring(7);
                exchange.getAttributes().put("jwtToken", jwtToken);
                logger.info("-----> Token almacenado en el contexto de seguridad de la aplicación");
            } else {
                logger.info("-----> No se encontró el token o no empieza con Bearer");
            }

            return chain.filter(exchange);
        };
    }



    public static class Config {

        //??
    }
}
