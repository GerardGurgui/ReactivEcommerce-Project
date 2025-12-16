package ReactiveEcommerce.ProductCatalog_service.DTO.output;

import ReactiveEcommerce.ProductCatalog_service.DTO.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductsResponseDto {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Boolean active;
    private CategoryDto category;
}
