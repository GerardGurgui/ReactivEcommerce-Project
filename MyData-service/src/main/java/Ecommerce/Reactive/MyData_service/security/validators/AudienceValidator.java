package Ecommerce.Reactive.MyData_service.security.validators;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.logging.Logger;

// Validator to check if the JWT contains the required audience
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final static Logger LOGGER = Logger.getLogger(AudienceValidator.class.getName());

    private final String requiredAudience;

    public AudienceValidator(String requiredAudience) {
        this.requiredAudience = requiredAudience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {

        List<String> audiences = token.getAudience();

        if (audiences == null || audiences.isEmpty()) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Audience claim missing", null)
            );
        }

        if (!audiences.contains(requiredAudience)) {
            LOGGER.warning("Audience validation FAILED: expected \""
                    + requiredAudience + "\" but found " + audiences);
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Invalid audience", null));
        }

        LOGGER.info("Audience validation successful: expected \"" + requiredAudience + "\" and found " + audiences);
        return OAuth2TokenValidatorResult.success();
    }

}
