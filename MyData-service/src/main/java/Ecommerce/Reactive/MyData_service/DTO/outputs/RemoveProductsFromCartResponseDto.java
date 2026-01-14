package Ecommerce.Reactive.MyData_service.DTO.outputs;

import Ecommerce.Reactive.MyData_service.DTO.carts.CartStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class RemoveProductsFromCartResponseDto {

    private String message;
    private Long cartId;
    private Long productId;
    private String cartName;
    private String productName;
    private CartStatus cartStatus;
    private Integer cartTotalItems;
    private BigDecimal cartTotalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
