package Ecommerce.Reactive.UserAuthentication_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("security.datasource")
public class SecurityConfigDataSource {

    private String signUpUrl;
    private String secret;
    private String tokenPrefix;
    private Integer expiresIn;
    private String authoritiesClaim;
    private String error;
    private String userManagementUrl;
}
