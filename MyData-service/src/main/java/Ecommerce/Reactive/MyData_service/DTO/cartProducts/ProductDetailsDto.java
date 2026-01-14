package Ecommerce.Reactive.MyData_service.DTO.cartProducts;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDetailsDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean active;
    private Integer stock;

}
