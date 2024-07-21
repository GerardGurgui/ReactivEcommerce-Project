package Ecommerce.usermanagement.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto{

    private Long id;
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
}
