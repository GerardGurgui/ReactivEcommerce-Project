package Ecommerce.Reactive.MyData_service.DTO.cartProducts;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddProductToCartRequestDto {

    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID can't be less than 1")
    private Long productId;
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity can't be less than 1")
    @Max(value = 99, message = "Quantity can't be more than 99")
    private Integer quantity;

}
