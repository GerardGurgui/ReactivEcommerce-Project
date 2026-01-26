package Ecommerce.usermanagement.dto.output;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserOwnProfileDto {

    private String uuid;
    //personal info
    private String username;
    private String name;
    private String email;
    private String phone;
    //additional info
    private Instant registeredAt;
    private String bio;
    private String profilePictureUrl;
    //roles
    private List<String> roles;
    //purchase statistics
    private Long totalPurchase;
    private BigDecimal totalSpent;

}
