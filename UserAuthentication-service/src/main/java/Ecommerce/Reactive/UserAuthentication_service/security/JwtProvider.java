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
import java.util.logging.Logger;

//clase encargada de generar los tokens JWT del usuario y de validarlos

@Component
public class JwtProvider {

    private static final Logger LOGGER =  Logger.getLogger(JwtProvider.class.getName());

    @Autowired
    private SecurityConfigDataSource securityConfigDataSource;


    // Generar un token JWT con el nombre de usuario y los roles del usuario
    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + securityConfigDataSource.getExpiresIn()))
                .signWith(getKey(securityConfigDataSource.getSecret()))
                .compact();
    }

    // Extraer y devolver las reclamaciones (claims) del token JWT
    public Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getKey(securityConfigDataSource.getSecret()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //
    public String getSubject(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getKey(securityConfigDataSource.getSecret()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Validar un token JWT y devolver verdadero si el token es válido
    public boolean validate(String token){

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey(securityConfigDataSource.getSecret()))
                    .build()
                    .parseClaimsJws(token);
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
