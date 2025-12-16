package ReactiveEcommerce.ProductCatalog_service.service;

import ReactiveEcommerce.ProductCatalog_service.DTO.CategoryDto;
import ReactiveEcommerce.ProductCatalog_service.DTO.output.ProductsResponseDto;
import ReactiveEcommerce.ProductCatalog_service.entity.Category;
import ReactiveEcommerce.ProductCatalog_service.entity.Product;
import ReactiveEcommerce.ProductCatalog_service.exceptions.CategoryNotFoundException;
import ReactiveEcommerce.ProductCatalog_service.exceptions.ProductNotFoundException;
import ReactiveEcommerce.ProductCatalog_service.repository.ICategoryRepository;
import ReactiveEcommerce.ProductCatalog_service.repository.IProductRepository;
import io.netty.handler.timeout.TimeoutException;
import io.r2dbc.spi.R2dbcException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Slf4j
@Service
public class ProductCatalogService {

    private final Logger logger = Logger.getLogger(ProductCatalogService.class.getName());

    private final ICategoryRepository categoryRepository;
    private final IProductRepository productRepository;

    public ProductCatalogService(ICategoryRepository categoryRepository,
                                 IProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    //Utilities
    private Mono<ProductsResponseDto> buildProductDtoWithCategory(Product product) {

        return categoryRepository.findById(product.getCategoryId())
                .switchIfEmpty(Mono.error(new CategoryNotFoundException(product.getCategoryId())))
                .map(category -> ProductsResponseDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .active(product.getActive())
                        .category(CategoryDto.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .build())
                        .build())
                .doOnError(e -> log.error("Error building product DTO: {}", e.getMessage(), e));
    }

    // ------> CRUD
    // ----> GETS

    // --> Get All
    public Flux<ProductsResponseDto> getAllProducts() {

        return productRepository.findAll()
                .flatMap(this::buildProductDtoWithCategory)
                .doOnComplete(() -> logger.info("Successfully fetched all products"))
                .onErrorResume(this::handleProductFetchError);
    }

    // --> Get Active Products

    public Flux<ProductsResponseDto> getActiveProducts() {

        return productRepository.findByActiveTrue()
                .flatMap(this::buildProductDtoWithCategory)
                .doOnComplete(() -> logger.info("Successfully fetched all active products"))
                .onErrorResume(this::handleProductFetchError);
    }

    // --> Get Products by Name

    public Flux<ProductsResponseDto> getProductsByName(String name) {

        // trim to avoid leading/trailing spaces
        String nameTrimmed = name.trim();

        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(nameTrimmed)
                .switchIfEmpty(Flux.defer(() -> {
                    log.info("No products found matching name: {}", nameTrimmed);
                    return Flux.empty();
                }))
                .flatMap(this::buildProductDtoWithCategory)
                .onErrorResume(this::handleProductFetchError);
    }

    // --> Get Products by Price Range

    public Flux<ProductsResponseDto> getProductsByPriceRange(Double minPrice, Double maxPrice) {

        if (minPrice > maxPrice) {
            return Flux.error(new IllegalArgumentException("min price cannot be greater than max price"));
        }

        return productRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice)
                .flatMap(this::buildProductDtoWithCategory)
                .doOnComplete(() -> logger.info("Successfully fetched products by price range: " + minPrice + " - " + maxPrice))
                .onErrorResume(this::handleProductFetchError);
    }

    // --> Get Active Products Ordered by Price Ascending

    public Flux<ProductsResponseDto> getActiveProductsOrderedByPriceAsc() {

        return productRepository.findByActiveTrueOrderByPriceAsc()
                .flatMap(this::buildProductDtoWithCategory)
                .doOnComplete(() -> logger.info("Successfully fetched active products ordered by price ascending"))
                .onErrorResume(this::handleProductFetchError);
    }

    // --> Get Active Products Ordered by Price Descending

    public Flux<ProductsResponseDto> getActiveProductsOrderedByPriceDesc() {

        return productRepository.findByActiveTrueOrderByPriceDesc()
                .flatMap(this::buildProductDtoWithCategory)
                .doOnComplete(() -> logger.info("Successfully fetched active products ordered by price descending"))
                .onErrorResume(this::handleProductFetchError);
    }

    // --> Check if Product Exists and is Active (for Cart Validation)

    public Mono<Boolean> checkProductExistsAndActive(Long productId) {

        return productRepository.existsByIdAndActiveTrue(productId)
                .doOnSuccess(exists -> logger.info("Product ID " + productId + " exists and active: " + exists))
                .doOnError(e -> log.error("Error checking product existence: {}", e.getMessage(), e));
    }

    // ----> CATEGORY BASED QUERIES

    // --> Get Products by Category id - if the product's category_id does not exist -> error 500

    public Flux<ProductsResponseDto> getProductsByCategoryId(Long categoryId) {

        return findCategoryById(categoryId)
                .flatMapMany(this::findProductsForCategory)
                .switchIfEmpty(Flux.defer(() -> {
                    logger.warning("No products found for category ID: " + categoryId);
                    return Flux.empty();
                }))
                .onErrorResume(this::handleProductFetchError);
    }

    // --> Get Products by Category Name - if the product's category_name doesn't exist return empty list

    public Flux<ProductsResponseDto> getProductsByCategoryName(String categoryName) {

        String categoryNameTrimmed = categoryName.trim();

        return findCategoryByName(categoryNameTrimmed)
                .flatMapMany(this::findProductsForCategory)
                .switchIfEmpty(Flux.defer(() -> {
                    logger.warning("No products found for category name: " + categoryNameTrimmed);
                    return Flux.empty();
                }))
                .onErrorResume(this::handleProductFetchError);
    }

    private Mono<Category> findCategoryByName(String categoryName) {

        return categoryRepository.findByNameIgnoreCase(categoryName);
    }

    private Mono<Category> findCategoryById(Long categoryId) {

        return categoryRepository.findById(categoryId);
    }

    private Flux<ProductsResponseDto> findProductsForCategory(Category category) {

        return productRepository.findByCategoryIdAndActiveTrue(category.getId())
                .flatMap(this::buildProductDtoWithCategory);
    }

    // Handle Errors
    private Flux<ProductsResponseDto> handleProductFetchError(Throwable error) {

        if (error instanceof CategoryNotFoundException) {
            log.error("Category not found: {}", error.getMessage());
            return Flux.error(error);
        }

        if (error instanceof ProductNotFoundException) {
            log.error("Product not found: {}", error.getMessage());
            return Flux.error(error);
        }

        if (error instanceof ConstraintViolationException){
            log.error("Validation error: {}", error.getMessage());
            return Flux.error(new RuntimeException("Validation error: " + error.getMessage(), error));
        }

        if (error instanceof R2dbcException) {
            log.error("Database error: {}", error.getMessage(), error);
            return Flux.error(new RuntimeException("Database connection error", error));
        }

        if (error instanceof TimeoutException) {
            log.error("Timeout fetching products: {}", error.getMessage());
            return Flux.error(new RuntimeException("Request timeout", error));
        }

        // Generic error handler
        log.error("Unexpected error fetching products: {}", error.getMessage(), error);
        return Flux.error(new RuntimeException("Failed to fetch products", error));
    }
}
