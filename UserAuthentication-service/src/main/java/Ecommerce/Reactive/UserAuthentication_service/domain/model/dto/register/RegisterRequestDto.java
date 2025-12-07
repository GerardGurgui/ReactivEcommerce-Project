package Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequestDto {

    private String username;
    private String email;
    private String password;
    private String name;
    private String lastName;
    private String phone;


}
