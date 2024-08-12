package Ecommerce.Reactive.ApiGateway_service.jwt;

import Ecommerce.Reactive.ApiGateway_service.security.config.SecurityConfigDataSource;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.logging.Logger;

@Component
public class JwtUtil {

    private static final Logger LOGGER =  Logger.getLogger(JwtUtil.class.getName());

    private final SecurityConfigDataSource securityConfigDataSource;

    @Autowired
    public JwtUtil(SecurityConfigDataSource securityConfigDataSource) {
        this.securityConfigDataSource = securityConfigDataSource;
    }

    // Validar un token JWT y devolver verdadero si el token es válido
    public Claims validate(String token){

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey(securityConfigDataSource.getSecret()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            LOGGER.severe("token expired");
        } catch (UnsupportedJwtException e) {
            LOGGER.severe("token unsupported");
        } catch (MalformedJwtException e) {
            LOGGER.severe("token malformed");
        } catch (SignatureException e) {
            LOGGER.severe("bad signature");
        } catch (IllegalArgumentException e) {
            LOGGER.severe("illegal args");
        }
        return null;
    }

    //REVISAR
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private SecretKey getKey(String secret) {
        try {
            byte[] secretBytes = Decoders.BASE64URL.decode(secret);
            return Keys.hmacShaKeyFor(secretBytes);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

}
