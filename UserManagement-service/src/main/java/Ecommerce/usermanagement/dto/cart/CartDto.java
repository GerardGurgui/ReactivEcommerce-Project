package Ecommerce.usermanagement.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {

    private String userUuid;

    @Pattern(regexp = "^[a-zA-Z0-9 ]*$")
    @Size(max = 20, message = "Name must be a maximum than 20 characters")
    private String name;

    private int totalProducts;
    private double totalPrice;
    private CartStatus status;

}
