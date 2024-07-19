package Ecommerce.Reactive.ApiGateway_service.jwt;

import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.logging.Logger;


// clase encargada de extraer el token de la cabecera de la petición y validar su autenticidad
// almacenando el token en el contexto de seguridad de la aplicación


@Component
public class JwtTokenFilter implements WebFilter {

    private static final Logger logger = Logger.getLogger(JwtTokenFilter.class.getName());

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // Excluir peticiones login y/o registro de usuario
        if (exchange.getRequest().getURI().getPath().contains("/auth/login")
                || exchange.getRequest().getURI().getPath().contains("api/usermanagement/addUser")) {
            logger.info("-----> Excluyendo peticiones login y addUser");
            return chain.filter(exchange);
        }

        // Extraer el token de la cabecera de la petición
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        logger.info("-----> TODA SOLICITUD EXCEPTO LOGIN y addUser PASA POR AQUI");

        // Validar que el token empiece con Bearer y almacenarlo en el contexto de seguridad de la aplicación
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(getKey(jwtSecret))
                        .parseClaimsJws(jwtToken)
                        .getBody();

                String username = claims.getSubject();
                if (username != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, null);

                    SecurityContext securityContext = new SecurityContextImpl(authentication);
                    logger.info("-----> Token válido, almacenando en el contexto de seguridad");
                    logger.info("-----> Usuario: " + username);
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                }
            } catch (Exception e) {
                logger.info("-----> Token inválido" + e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } else {
            logger.info("-----> No se encontró el token o no empieza con Bearer");
        }

        return chain.filter(exchange);
    }

    private SecretKey getKey(String secret) {
        try {
            byte[] secretBytes = Decoders.BASE64URL.decode(secret);
            return Keys.hmacShaKeyFor(secretBytes);
        } catch (IllegalArgumentException e) {
            logger.severe("-----> La clave secreta no está en el formato correcto: " + e.getMessage());
            throw e;
        }
    }



}
