package Ecommerce.Reactive.ApiGateway_service.security.config.validators;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.logging.Logger;

public class UserUuidValidator implements OAuth2TokenValidator<Jwt> {

    private static final Logger LOGGER = Logger.getLogger(UserUuidValidator.class.getName());

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String subject = token.getSubject();

        if (subject != null && !subject.trim().isEmpty()) {
            LOGGER.info("User UUID validation successful: subject claim is present and not empty");
            return OAuth2TokenValidatorResult.success();
        }

        OAuth2Error error = new OAuth2Error(
                "invalid_subject",
                "The token does not contain a valid subject (sub) claim",
                null
        );

        return OAuth2TokenValidatorResult.failure(error);
    }
}