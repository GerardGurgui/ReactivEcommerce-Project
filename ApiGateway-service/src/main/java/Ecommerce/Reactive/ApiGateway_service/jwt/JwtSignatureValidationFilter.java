//package Ecommerce.Reactive.ApiGateway_service.jwt;
//
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ResponseStatusException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.SignatureException;
//
//import java.security.PublicKey;
//import java.security.cert.CertificateFactory;
//import java.util.logging.Logger;
//
////clase encargada de validar la firma del token JWT recibido en la cabecera de la petición
////utilizando la clave pública almacenada en el archivo publicKey.cert
//
//@Component
//public class JwtSignatureValidationFilter extends AbstractGatewayFilterFactory<JwtSignatureValidationFilter.Config> {
//
//    private static final Logger logger = Logger.getLogger(JwtSignatureValidationFilter.class.getName());
//    private PublicKey publicKey;
//
//    public JwtSignatureValidationFilter() {
//        super(Config.class);
//        try {
//            publicKey = CertificateFactory.getInstance("X.509")
//                    .generateCertificate(new ClassPathResource("publicKey.cert").getInputStream())
//                    .getPublicKey();
//            logger.info("Public key loaded successfully");
//        } catch (Exception e) {
//            logger.severe("Error loading public key: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            logger.info("Validating JWT signature");
//            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                try {
//                    Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
//                    logger.info("JWT signature validated successfully");
//                    return chain.filter(exchange);
//                } catch (SignatureException e) {
//                    logger.warning("Invalid JWT signature: " + e.getMessage());
//                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT signature");
//                }
//            }
//            logger.warning("No JWT token found in Authorization header");
//            return chain.filter(exchange);
//        };
//    }
//
//    public static class Config {
//
//    }
//}
//
//
