package Ecommerce.Reactive.MyData_service.controller;

import Ecommerce.Reactive.MyData_service.entity.Product;
import Ecommerce.Reactive.MyData_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/MyData/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public Mono<Product> addProduct(@RequestBody Product product) {

       return productService.addProduct(product);

    }

    @GetMapping("/get/{id}")
    public Mono<Product> getProductById(@PathVariable Long id) {

        return productService.getProductById(id);
    }

}
