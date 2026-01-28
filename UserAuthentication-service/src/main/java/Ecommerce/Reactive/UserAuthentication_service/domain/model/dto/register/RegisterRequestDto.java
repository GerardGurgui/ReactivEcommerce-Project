package Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "The username is required")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$", message = "username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "The email is required")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email must be a valid email")
    private String email;

    @NotBlank(message = "The password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "password must be at least 8 characters long and contain at least one uppercase letter," +
                    " one lowercase letter, one number and one special character")
    private String password;

    @NotBlank(message = "The first name is required")
    private String name;

    @NotBlank(message = "The last name is required")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "phone number must be a valid format")
    private String phone;


}
