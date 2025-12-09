package Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.login;

import Ecommerce.Reactive.UserAuthentication_service.domain.roles.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {

    private String uuid;
    private String username;
    private String email;

    @ToString.Exclude
    private String password;

    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isEnabled;
    private Set<Roles> roles;


}
