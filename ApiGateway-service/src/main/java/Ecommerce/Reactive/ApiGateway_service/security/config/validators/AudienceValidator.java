package Ecommerce.Reactive.ApiGateway_service.security.config.validators;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.logging.Logger;

// Validator to check if the JWT contains the required audience
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String expectedAudience;

    public AudienceValidator(String expectedAudience) {
        this.expectedAudience = expectedAudience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {

        List<String> audience = token.getAudience();

        if (audience == null || audience.isEmpty()) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Missing audience (aud) claim", null)
            );
        }

        if (audience.contains(expectedAudience)) {
            return OAuth2TokenValidatorResult.success();
        }

        return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token",
                        "Audience mismatch: expected " + expectedAudience + " but was: " + audience, null)
        );
    }
}
