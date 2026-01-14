package Ecommerce.Reactive.MyData_service.DTO.cartProducts;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ResponseProductToCartDto {

    // details of the product added
    private Long id;
    private String name;
    private BigDecimal price;

    // actual cart details
    private Long cartId;
    private Integer cartTotalItems;
    private BigDecimal cartTotalPrice;

    private String message;
}
