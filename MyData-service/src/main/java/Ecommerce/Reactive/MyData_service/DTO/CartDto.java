package Ecommerce.Reactive.MyData_service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {

    private String userUuid;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$")
    @Size(max = 20, message = "Name must be a maximum than 20 characters")
    private String name;

    private int totalProducts;
    private double totalPrice;


}
