package Ecommerce.usermanagement.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationResponseDto {

    private String message;
    private String userName;
}
