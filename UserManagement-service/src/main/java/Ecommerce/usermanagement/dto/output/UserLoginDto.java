package Ecommerce.usermanagement.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto{

    private String username;
    private String email;
    private String password;
}
