package ReactiveEcommerce.ProductCatalog_service.DTO.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailsDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean active;
    private Integer stock;
}
