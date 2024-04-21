package Ecommerce.Reactive.MyData_service.service;

import Ecommerce.Reactive.MyData_service.DTO.ProductDTO;
import Ecommerce.Reactive.MyData_service.entity.Product;
import Ecommerce.Reactive.MyData_service.mapping.IMapper;
import Ecommerce.Reactive.MyData_service.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    /*
    * comprobaciones pendientes:
    * - ¿Se debería de hacer una comprobación de si el producto ya existe en la base de datos?
    * - ¿Se debería de hacer una comprobación de si el producto tiene todos los campos necesarios?
    * - ¿Se debería de hacer una comprobación de si el producto tiene un precio válido?
    * - ¿Se debería de hacer una comprobación de si el producto tiene un stock válido?
    * - algunas mas?
    * */

    private final IProductRepository productRepository;

    @Autowired
    private IMapper mapper;

    @Autowired
    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Mono<Product> addProduct(ProductDTO productDto) {

        return mapper.ProductDtoToProduct(productDto)
                .flatMap(product -> productRepository.save(product));

    }

    public Mono<Product> getProductById(Long id) {

        return productRepository.findById(id);
    }

}
