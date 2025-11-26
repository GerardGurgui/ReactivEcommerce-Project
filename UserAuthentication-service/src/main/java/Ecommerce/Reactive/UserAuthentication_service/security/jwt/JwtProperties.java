package Ecommerce.Reactive.UserAuthentication_service.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
@Data
public class JwtProperties {

    private String secret;
    private String issuer;
    private long expirationMs;
    private List<String> audiences;
}

