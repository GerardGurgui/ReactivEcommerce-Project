package Ecommerce.usermanagement.dto.input;

import Ecommerce.usermanagement.dto.cart.CartDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String uuid;
    private String username;
    private String email;
    private Long CartId;
    private List<CartDto> carts;

    public UserDto addCart(CartDto cartDto) {

        if (carts == null) {
            carts = new ArrayList<>();
        }
        carts.add(cartDto);
        return this;
    }
}
