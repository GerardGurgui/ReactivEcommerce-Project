package ReactiveEcommerce.ProductCatalog_service.controller;

import ReactiveEcommerce.ProductCatalog_service.DTO.output.ProductDetailsDto;
import ReactiveEcommerce.ProductCatalog_service.service.ProductCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/internal")
public class InternalProductController {

    private final ProductCatalogService productCatalogService;

    public InternalProductController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    // --> COMMUNICATION WITH MYDATA-SERVICE TO GET PRODUCT DETAILS <--

    @GetMapping("/products/{id}")
    public Mono<ProductDetailsDto> getProductForMyData(@PathVariable Long id) {

        return productCatalogService.getProductToMyData(id);
    }
}
