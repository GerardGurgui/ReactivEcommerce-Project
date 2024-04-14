package Ecommerce.Reactive.MyData_service.service;

import Ecommerce.Reactive.MyData_service.DTO.ProductDTO;
import Ecommerce.Reactive.MyData_service.entity.Product;
import Ecommerce.Reactive.MyData_service.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    @Autowired
    private IProductRepository productRepository;


    public Mono<Product> addProduct(Product product) {

        return productRepository.save(product);
    }

    public Mono<Product> getProductById(Long id) {

        return productRepository.findById(id);
    }

}
