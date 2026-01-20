package Ecommerce.usermanagement.dto.output;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
// No uuid included for privacy reasons, public profile info only
public class UserProfileDto {

    private String username;
    private String email;
    private String name;
    private Instant createdAt;

}
