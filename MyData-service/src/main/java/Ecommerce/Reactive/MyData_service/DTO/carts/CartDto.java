package Ecommerce.Reactive.MyData_service.DTO.carts;

import Ecommerce.Reactive.MyData_service.entity.CartProduct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDto {

    private String userUuid;

    @NotNull
    @NotBlank(message = "Cart name is required")
    @Size(min = 1, max = 50, message = "Cart name must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s\\-_]+$",
            message = "Cart name can only contain letters, numbers, spaces, hyphens and underscores")
    private String name;
    private Long id;
    private CartStatus status;
    private Integer cartTotalItems;
    private Double cartTotalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartProduct> products;


}
