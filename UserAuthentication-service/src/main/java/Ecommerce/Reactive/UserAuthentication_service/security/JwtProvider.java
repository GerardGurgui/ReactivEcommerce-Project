package Ecommerce.Reactive.UserAuthentication_service.security;

import Ecommerce.Reactive.UserAuthentication_service.config.SecurityConfigDataSource;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

//clase encargada de generar los tokens JWT del usuario y de validarlos

@Component
public class JwtProvider {

    private static final Logger LOGGER =  Logger.getLogger(JwtProvider.class.getName());

    private final JwtProperties jwtProperties;

    public JwtProvider(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
    }

    // Generate a JWT token for the given user details and user UUID, issuer, audience, roles, issued at and expiration
    public String generateToken(UserDetails userDetails, String userUuid) {

        return Jwts.builder()
                .setSubject(userUuid)
                .setIssuer(jwtProperties.getIssuer())
                .claim("aud", jwtProperties.getAudiences())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .signWith(getKey(jwtProperties.getSecret()), SignatureAlgorithm.HS256)
                .compact();
    }


    // Validar un token JWT y devolver verdadero si el token es válido
    public boolean validate(String token){

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey(jwtProperties.getSecret()))
                    .build()
                    .parseClaimsJws(token);
            LOGGER.info("---> token is valid from Auth service");
            return true;

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
        return false;
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
