package Ecommerce.usermanagement.dto.input;

import Ecommerce.usermanagement.document.Roles;
import lombok.*;

import java.time.Instant;
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


    private String role; // "USER", "ADMIN", etc.
}