package Ecommerce.usermanagement.security.config.validators;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.logging.Logger;

// Validator to check if the JWT contains the required audience
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String requiredAudience;
    private final Logger LOGGER = Logger.getLogger(AudienceValidator.class.getName());

    public AudienceValidator(String requiredAudience) {
        this.requiredAudience = requiredAudience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {

        if (token.getAudience().contains(requiredAudience)) {
            LOGGER.info("Audience validation successful: expected " + requiredAudience + " and found " + token.getAudience());
            return OAuth2TokenValidatorResult.success();
        }

        OAuth2Error error = new OAuth2Error("invalid_token",
                "The required audience " + requiredAudience + " is missing",
                null);

        return OAuth2TokenValidatorResult.failure(error);
    }
}
