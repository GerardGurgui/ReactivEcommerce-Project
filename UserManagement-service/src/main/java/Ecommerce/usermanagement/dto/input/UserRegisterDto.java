package Ecommerce.usermanagement.dto.input;

import Ecommerce.usermanagement.document.Role;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {

    // identifyer
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

    // Role
    private String role;
}