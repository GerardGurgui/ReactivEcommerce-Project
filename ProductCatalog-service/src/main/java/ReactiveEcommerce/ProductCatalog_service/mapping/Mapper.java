package ReactiveEcommerce.ProductCatalog_service.mapping;

import ReactiveEcommerce.ProductCatalog_service.DTO.ProductDTO;
import ReactiveEcommerce.ProductCatalog_service.entity.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Mapper implements IMapper {


    @Override
    public Mono<Product> ProductDtoToProduct(ProductDTO productDTO) {

        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        return Mono.just(product);

    }


    @Override
    public Mono<ProductDTO> mapProductToProductDto(Product product) {

        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);
        return Mono.just(productDTO);
    }


}
