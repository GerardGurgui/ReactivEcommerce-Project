package Ecommerce.Reactive.UserAuthentication_service.security.userdetails.service;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.roles.Roles;
import Ecommerce.Reactive.UserAuthentication_service.security.userdetails.UserDetailsImpl;
import Ecommerce.Reactive.UserAuthentication_service.service.usermanagement.UserManagementConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final Logger logger = Logger.getLogger(CustomUserDetailsService.class.getName());
    private final UserManagementConnectorService userManagementConnectorService;

    @Autowired
    public CustomUserDetailsService(UserManagementConnectorService userManagementConnectorService) {
        this.userManagementConnectorService = userManagementConnectorService;
    }

    @Override
    public Mono<UserDetails> findByUsername(String usernameOrEmail) {

         return getUserByUsernameOrEmail(usernameOrEmail)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .map(user -> new UserDetailsImpl(
                        user.getUuid(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPassword(),
                        user.isAccountNonExpired(),
                        user.isAccountNonLocked(),
                        user.isCredentialsNonExpired(),
                        user.isEnabled(),
                        convertRolesToAuthorities(user.getRoles())
                ));
    }

    private Mono<UserLoginDto> getUserByUsernameOrEmail(String usernameOrEmail) {

        if (usernameOrEmail.contains("@")) {
            return userManagementConnectorService.getUserByEmail(usernameOrEmail);
        } else {
            return userManagementConnectorService.getUserByUsername(usernameOrEmail);
        }
    }


    private Collection<? extends GrantedAuthority> convertRolesToAuthorities(Set<Roles> roles) {

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

}
