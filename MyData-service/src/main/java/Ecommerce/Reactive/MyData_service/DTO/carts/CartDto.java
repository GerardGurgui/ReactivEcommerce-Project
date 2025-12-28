package Ecommerce.Reactive.MyData_service.DTO.carts;

import Ecommerce.Reactive.MyData_service.entity.CartProduct;
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
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$")
    @Size(max = 20, message = "Name must be a maximum than 20 characters")
    private String name;
    private CartStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartProduct> products;


}
