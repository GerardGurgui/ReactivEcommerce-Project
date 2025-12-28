package Ecommerce.Reactive.MyData_service.DTO.cartProducts;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDetailsFromMsDto {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Boolean active;
    private Integer stock;

}
