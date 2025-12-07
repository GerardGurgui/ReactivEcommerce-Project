package Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RegistrationResponseDto {

    private String uuid;
    private String message;
    private String email;
    private String username;
    private Instant registeredAt;
}