package Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register;

import Ecommerce.Reactive.UserAuthentication_service.domain.roles.Role;
import lombok.*;
import org.apache.kafka.common.protocol.types.Field;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterInternalDto {

    // identifyers
    private String uuid;

    // Credentiales
    private String username;
    private String email;
    private String passwordHash;

    // personal info
    private String name;
    private String lastName;
    private String phone;

    // Metadata
    private String registrationIp;
    private Instant registeredAt;

    private String role;
}