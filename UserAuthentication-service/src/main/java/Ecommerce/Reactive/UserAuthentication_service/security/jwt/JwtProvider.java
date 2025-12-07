package Ecommerce.Reactive.UserAuthentication_service.security.jwt;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.login.UserLoginDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
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
    public String generateToken(UserLoginDto userLoginDto) {

        return Jwts.builder()
                .setSubject(userLoginDto.getUuid())
                .setIssuer(jwtProperties.getIssuer())
                .claim("aud", jwtProperties.getAudiences())
                .claim("roles", userLoginDto.getRoles().stream().map(Enum::name).toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .signWith(getKey(jwtProperties.getSecret()), SignatureAlgorithm.HS256)
                .compact();
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
