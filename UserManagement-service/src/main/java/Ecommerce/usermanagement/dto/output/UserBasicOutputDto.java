package Ecommerce.usermanagement.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserBasicOutputDto {

    private String uuid;
    private String username;
    private String email;

}
