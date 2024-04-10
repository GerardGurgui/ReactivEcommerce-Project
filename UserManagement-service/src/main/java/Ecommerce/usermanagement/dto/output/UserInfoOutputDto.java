package Ecommerce.usermanagement.dto.output;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfoOutputDto {

    private String username;
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private Long totalPurchase;
    private int totalSpent;
    private boolean isActive;
    private boolean activeCart;
    private LocalDate loginDate;
    private String latestAcces;
    private Set<String> roles;

}
