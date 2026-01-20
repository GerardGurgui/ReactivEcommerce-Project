package Ecommerce.usermanagement.dto.output;


import Ecommerce.usermanagement.document.Roles;
import Ecommerce.usermanagement.dto.cart.CartDto;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserInfoOutputDto {

    private String username;
    private String name;
    private String lastname;
    private String phone;
    private String email;
    private Long totalPurchase;
    private int totalSpent;
    private boolean isActive;
    private LocalDate loginDate;
    private Instant latestAccess;
    private List<String> roles;
    private List<Long> cartsId;

}
