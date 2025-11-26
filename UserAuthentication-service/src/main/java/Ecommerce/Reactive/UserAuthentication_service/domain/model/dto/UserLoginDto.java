package Ecommerce.Reactive.UserAuthentication_service.domain.model.dto;

import Ecommerce.Reactive.UserAuthentication_service.domain.roles.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto{

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
