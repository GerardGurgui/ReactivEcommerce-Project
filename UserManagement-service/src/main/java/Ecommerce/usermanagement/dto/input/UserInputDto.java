package Ecommerce.usermanagement.dto.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInputDto {

    @NotNull(message = "The username is required")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$", message = "username must be between 3 and 20 characters")
    private String username;

    @NotNull(message = "The first name is required")
    private String firstname;

    @NotNull(message = "The last name is required")
    private String lastname;

    private String phone;

    @NotNull(message = "The email is required")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email must be a valid email")
    private String email;

    @NotNull(message = "The password is required")
    private String password;


}
