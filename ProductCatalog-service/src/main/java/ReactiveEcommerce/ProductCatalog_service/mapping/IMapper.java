package ReactiveEcommerce.ProductCatalog_service.mapping;

import ReactiveEcommerce.ProductCatalog_service.DTO.ProductDTO;
import ReactiveEcommerce.ProductCatalog_service.entity.Product;
import reactor.core.publisher.Mono;

public interface IMapper {

    Mono<Product> ProductDtoToProduct(ProductDTO input);

    Mono<ProductDTO> mapProductToProductDto(Product product);
}
