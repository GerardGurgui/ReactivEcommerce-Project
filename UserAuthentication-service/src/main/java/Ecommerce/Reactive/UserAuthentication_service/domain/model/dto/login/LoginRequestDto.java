package Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {


    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$", message = "username must be between 3 and 20 characters")
    private String username;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email must be a valid email")
    private String email;

    @NotBlank(message = "The password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "password must be at least 8 characters long and contain at least one uppercase letter," +
                    " one lowercase letter, one number and one special character")
    private String password;
}

