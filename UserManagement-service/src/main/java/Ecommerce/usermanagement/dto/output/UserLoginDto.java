package Ecommerce.usermanagement.dto.output;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto{

    private String uuid;
    private String username;
    private String email;
    private String password;
    private List<String> roles;
    //Properties from userDetails
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isEnabled;


}
