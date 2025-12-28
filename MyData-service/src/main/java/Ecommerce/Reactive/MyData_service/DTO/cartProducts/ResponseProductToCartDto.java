package Ecommerce.Reactive.MyData_service.DTO.cartProducts;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseProductToCartDto {

    // details of the product added
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Boolean active;
    private Integer stock;

    // actual cart details
    private Long cartId;
    private Integer cartTotalItems;
    private Double cartTotalPrice;

    private String message;
}
