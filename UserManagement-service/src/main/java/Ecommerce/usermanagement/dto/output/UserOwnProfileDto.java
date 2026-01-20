package Ecommerce.usermanagement.dto.output;

import Ecommerce.usermanagement.document.Roles;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserOwnProfileDto {

    private String uuid;
    private String username;
    private String name;
    private String email;
    private String phone;
    private Instant registeredAt;
    private String bio;
    private String profilePictureUrl;
    private List<String> roles;

}
