package Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    private String username;
    private String email;
    private String password;
}

