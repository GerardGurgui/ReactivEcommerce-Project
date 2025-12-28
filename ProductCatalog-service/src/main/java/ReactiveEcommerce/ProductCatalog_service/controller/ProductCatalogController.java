package ReactiveEcommerce.ProductCatalog_service.controller;

import ReactiveEcommerce.ProductCatalog_service.DTO.output.ProductDetailsDto;
import ReactiveEcommerce.ProductCatalog_service.service.ProductCatalogService;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productcatalog")
@Validated
public class ProductCatalogController {

    private final ProductCatalogService  productCatalogService;

    public ProductCatalogController (ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    ///----------- GET METHODS --------------///

    @GetMapping("/getAllProducts")
    public Flux<ProductDetailsDto> getAllProducts() {

        return productCatalogService.getAllProducts();
    }

    @GetMapping("/getActiveProducts")
    public Flux<ProductDetailsDto> getActiveProducts() {

        return productCatalogService.getActiveProducts();
    }

    @GetMapping("/getByCategoryId")
    public Flux<ProductDetailsDto> getProductsByCategory(
            @RequestParam @Min(value = 1, message = "Id must be 1 or greater") Long categoryId) {

        return productCatalogService.getProductsByCategoryId(categoryId);
    }


    @GetMapping("/priceRange")
    public Flux<ProductDetailsDto> getProductsByPriceRange(
            @RequestParam
            @NotNull(message = "Minimum price is required")
            @Min(value = 0, message = "Minimum price must be 0 or greater")
            Double minPrice,

            @RequestParam
            @NotNull(message = "Maximum price is required")
            @Min(value = 0, message = "Maximum price must be 0 or greater")
            Double maxPrice) {

        return productCatalogService.getProductsByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/getByName")
    public Flux<ProductDetailsDto> searchProducts(
            @RequestParam("name")
            @NotBlank(message = "Product name is required and cannot be empty")
            @Size(min = 2, max = 30, message = "Product name must be between 2 and 30 characters")
            @Pattern(regexp = "^[a-zA-Z0-9\\s\\-]+$", message = "Product name can only contain letters, numbers and spaces")
            String name) {

        return productCatalogService.getProductsByName(name);
    }

    @GetMapping("/getByCategoryName")
    public Flux<ProductDetailsDto> getProductsByCategoryName(
            @RequestParam("categoryName")
            @NotBlank(message = "Category name is required and cannot be empty")
            @Size(min = 2, max = 30, message = "Category name must be between 2 and 30 characters")
            @Pattern(regexp = "^[a-zA-Z\\s\\-]+$", message = "Category name can only contain letters and spaces")
            String categoryName) {

        return productCatalogService.getProductsByCategoryName(categoryName);
    }

    @GetMapping("/getByOrderAscendingPrice")
    public Flux<ProductDetailsDto> getProductsByOrderAscendingPrice() {

        return productCatalogService.getActiveProductsOrderedByPriceAsc();
    }

    @GetMapping("/getByOrderDescendingPrice")
    public Flux<ProductDetailsDto> getProductsByOrderDescendingPrice() {

        return productCatalogService.getActiveProductsOrderedByPriceDesc();
    }

    @GetMapping("/checkProductAvailability")
    public Mono<Boolean> checkProductAvailability(@RequestParam @Min(1) Long productId) {

        return productCatalogService.checkProductExistsAndActive(productId);
    }

}
