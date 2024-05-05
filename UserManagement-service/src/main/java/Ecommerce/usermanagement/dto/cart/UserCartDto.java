package Ecommerce.usermanagement.dto.cart;

import Ecommerce.usermanagement.dto.input.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCartDto {

    private UserDto userDto;
    private CartDto cartDto;

}