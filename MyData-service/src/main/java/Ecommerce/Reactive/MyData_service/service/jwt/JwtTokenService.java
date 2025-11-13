package Ecommerce.Reactive.MyData_service.service.jwt;

import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.logging.Logger;

@Service
public class JwtTokenService {

    private static final Logger LOGGER =  Logger.getLogger(JwtTokenService.class.getName());

    private static final String CLAIM_UUID = "uuid";
    private static final String CLAIM_USERNAME = "preferred_username";
    private static final String CLAIM_EMAIL = "email";
    private static final String SUBJECT_CLAIM = "sub";

    public Mono<UserDto> extractUserFromToken() {

        LOGGER.info("-----> Extracting user from token in JwtTokenService");

        // Extract the JWT token from the security context and extract the user info from the "sub" claim

        return ReactiveSecurityContextHolder.getContext()
                // Extract Authentication from SecurityContext
                .map(securityContext -> securityContext.getAuthentication())
                // Filter for JwtAuthenticationToken
                .filter(authentication -> authentication instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                // Extract Jwt token
                .map(JwtAuthenticationToken::getToken)
                // Recover UserDto from Jwt claims
                .map(this::recoverUserFromClaims);
    }

    private UserDto recoverUserFromClaims(Jwt jwt) {

        Map<String, Object> claims = jwt.getClaims();
        UserDto user = new UserDto();

        // Fallback priority for uuid: CLAIM_UUID -> SUBJECT_CLAIM -> empty string
        Object uuid;

        // Extract UUID if present
        if (claims.containsKey(CLAIM_UUID)) {
            uuid = claims.get(CLAIM_UUID);
            LOGGER.info("UUID found in claims: " + uuid);
        }
        // else use subject claim
        else uuid = claims.getOrDefault(SUBJECT_CLAIM, "");
        user.setUuid(uuid != null ? uuid.toString() : "");

        // Extract username if present, else use subject claim
        Object username = claims.getOrDefault(CLAIM_USERNAME, jwt.getSubject());
        if (username != null) {
            user.setUsername(username.toString());
            LOGGER.info("Username found in claims: " + username);
        }

        // Extract email if present
        Object email = claims.get(CLAIM_EMAIL);
        if (email != null) {
            user.setEmail(email.toString());
            LOGGER.info("Email found in claims: " + email);
        }
        return user;
    }
}

