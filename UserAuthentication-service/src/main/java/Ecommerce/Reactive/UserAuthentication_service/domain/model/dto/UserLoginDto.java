package Ecommerce.Reactive.UserAuthentication_service.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Set<String> roles;
}
