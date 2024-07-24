package Ecommerce.usermanagement.dto.output;

import Ecommerce.usermanagement.document.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto{

    private String uuid;
    private String username;
    private String email;
    private String password;
    private Set<Roles> roles;
    //Properties from userDetails
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    public boolean isAdmin() {

        return roles.stream()
                .anyMatch(role -> role.equals(Roles.ROLE_ADMIN));

    }

}
