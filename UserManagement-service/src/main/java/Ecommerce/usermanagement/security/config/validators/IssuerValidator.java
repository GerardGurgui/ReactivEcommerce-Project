package Ecommerce.usermanagement.security.config.validators;

import Ecommerce.usermanagement.security.config.SecurityConfig;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.logging.Logger;

// Validator to check if the issuer of the JWT matches the expected issuer
public class IssuerValidator implements OAuth2TokenValidator<Jwt> {

    private final String expectedIssuer;
    private final Logger LOGGER = Logger.getLogger(IssuerValidator.class.getName());

    public IssuerValidator(String expectedIssuer) {
        this.expectedIssuer = expectedIssuer;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {

        String iss = token.getIssuer() != null ? token.getIssuer().toString() : null;

        if (expectedIssuer.equals(iss)) {
            LOGGER.info("Issuer validation successful: expected " + expectedIssuer + " and found " + iss);
            return OAuth2TokenValidatorResult.success();
        }

        OAuth2Error error = new OAuth2Error(
                "invalid_issuer",
                "Invalid token issuer: expected " + expectedIssuer + " but was " + iss,
                null
        );

        return OAuth2TokenValidatorResult.failure(error);
    }
}