package Ecommerce.Reactive.MyData_service.service;

import Ecommerce.Reactive.MyData_service.DTO.outputs.RemoveProductsFromCartResponseDto;
import Ecommerce.Reactive.MyData_service.DTO.cartProducts.AddProductToCartRequestDto;
import Ecommerce.Reactive.MyData_service.DTO.cartProducts.ProductDetailsDto;
import Ecommerce.Reactive.MyData_service.DTO.cartProducts.ResponseProductToCartDto;
import Ecommerce.Reactive.MyData_service.DTO.carts.CartStatus;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.entity.CartProduct;
import Ecommerce.Reactive.MyData_service.exceptions.*;
import Ecommerce.Reactive.MyData_service.mapping.IConverter;
import Ecommerce.Reactive.MyData_service.repository.ICartProductsRepository;
import Ecommerce.Reactive.MyData_service.repository.ICartRepository;
import Ecommerce.Reactive.MyData_service.security.SecurityUtils;
import Ecommerce.Reactive.MyData_service.service.communication.productsMs.ProductCatalogConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class CartProductService {

    private final ICartProductsRepository cartProductsRepository;
    private final ICartRepository cartRepository;
    private final SecurityUtils securityUtils;
    private final ProductCatalogConnectorService productCatalogConnectorService;
    private final TransactionalOperator transactionalOperator;

    @Value("${cart.product.max-quantity}")
    private Integer maxQuantityPerProduct;

    public CartProductService(ICartProductsRepository cartProductsRepository,
                              ICartRepository cartRepository,
                              SecurityUtils securityUtils,
                              ProductCatalogConnectorService productCatalogConnectorService,
                              TransactionalOperator transactionalOperator) {
        this.productCatalogConnectorService = productCatalogConnectorService;
        this.cartProductsRepository = cartProductsRepository;
        this.cartRepository = cartRepository;
        this.securityUtils = securityUtils;
        this.transactionalOperator = transactionalOperator;
    }

    // --> PENDIENTE: IMPLEMENTAR IDEMPOTENCY KEY CUANDO SE COMPLETE TRANSACCION CON CHECKOUT

    // ==================== CRUD ====================

    public Mono<ResponseProductToCartDto> addProductToCart(Long cartId, AddProductToCartRequestDto requestDto) {

        log.info("Adding product {} to cart {}, quantity: {}", requestDto.getProductId(), cartId, requestDto.getQuantity());

        return getActiveCartIfOwnedByCurrentUser(cartId)
                .flatMap(cart -> productCatalogConnectorService.getProductInfoFromProductsMs(requestDto.getProductId())
                        .flatMap(productInfo -> findOrCreateCartProduct(cart.getId(), requestDto, productInfo))
                        .flatMap(cartProduct -> updateCartTotals(cart, requestDto, cartProduct)
                                .map(savedCart -> buildAddProductResponse(savedCart, cartProduct))))
                .as(transactionalOperator::transactional) // Apply transactional operator to ensure atomicity of the operations
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .filter(throwable -> throwable instanceof OptimisticLockingFailureException)
                        .doBeforeRetry(signal -> log.warn("Optimistic locking conflict, retry attempt: {}", signal.totalRetries() + 1)))
                .doOnSuccess(response -> log.info("Product {} added to cart {} successfully", requestDto.getProductId(), cartId))
                .doOnError(error -> log.error("Failed to add product {} to cart {}: {}", requestDto.getProductId(), cartId, error.getMessage()));
    }

    public Mono<RemoveProductsFromCartResponseDto> removeProductsFromCart(Long cartId, Long productId) {

        log.info("Deleting product/s from cart {}, product: {}", cartId, productId);

        return getActiveCartIfOwnedByCurrentUser(cartId)
                .flatMap(cart -> cartProductsRepository.findByCartIdAndProductId(cartId, productId)
                        .flatMap(cartProduct ->
                                cartProductsRepository.deleteById(cartProduct.getId())
                                        .then(updateCartTotalsDelete(cart, cartProduct))
                                        .map(updatedCart -> buildRemoveProductResponse(updatedCart, cartProduct))
                        )
                        .switchIfEmpty(Mono.defer(() ->   // Handle case where product is not found in cart, already removed or never added
                                Mono.just(buildProductNotExistsInCartResponse(cartId, productId))
                        ))
                )
                .as(transactionalOperator::transactional) // Apply transactional operator to ensure atomicity of the operations
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .filter(throwable -> throwable instanceof OptimisticLockingFailureException))
                .doOnSuccess(response -> log.info("Product {} deleted from cart {} successfully", productId, cartId))
                .doOnError(error -> log.error("Failed to delete product {} from cart {}: {}", productId, cartId, error.getMessage()));
    }

    // ==================== CART VALIDATION ====================
    //  Verify that the cart belongs to the current user.

    private Mono<Cart> getActiveCartIfOwnedByCurrentUser(Long cartId) {

        log.debug("Validating cart {} ownership", cartId);

        return securityUtils.extractUserUuidFromJwt()
                .switchIfEmpty(Mono.error(new UserNotFoundException("User UUID not found in JWT token")))
                .flatMap(userUuid -> cartRepository.findById(cartId)
                        .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found with id: " + cartId)))
                        .flatMap(cart -> validateCartOwnership(cart, userUuid))
                        .flatMap(this::validateCartIsActive)
                );
    }

    // ==================== PRODUCT OPERATIONS ====================
    // Find existing cart product or create a new one, validating product availability and stock.

    private Mono<CartProduct> findOrCreateCartProduct(Long cartId,
                                                      AddProductToCartRequestDto requestDto,
                                                      ProductDetailsDto productInfo) {

        return validateProductAvailability(productInfo)
                .flatMap(validProduct -> cartProductsRepository.findByCartIdAndProductId(cartId, validProduct.getId())
                        .flatMap(existingProduct -> updateExistingCartProduct(existingProduct, requestDto, validProduct))
                        .switchIfEmpty(Mono.defer(() -> createNewCartProduct(cartId, requestDto, validProduct)))
                );
    }

    private Mono<CartProduct> updateExistingCartProduct(CartProduct existingProduct,
                                                        AddProductToCartRequestDto requestDto,
                                                        ProductDetailsDto productInfo) {

        Integer totalQuantity = existingProduct.getQuantity() + requestDto.getQuantity();

        return validateStockAndQuantity(existingProduct.getProductId(), totalQuantity, productInfo.getStock())
                .then(Mono.defer(() -> { // mono.defer to delay execution for validation
                    existingProduct.setQuantity(totalQuantity);
                    existingProduct.setUpdatedAt(LocalDateTime.now());
                    return cartProductsRepository.save(existingProduct);
                }))
                .doOnSuccess(savedProduct ->
                        log.debug("Updated existing cart product {} quantity to {}", savedProduct.getProductId(), savedProduct.getQuantity()));
    }

    private Mono<CartProduct> createNewCartProduct(Long cartId,
                                                   AddProductToCartRequestDto requestDto,
                                                   ProductDetailsDto productInfo) {

        log.debug("Creating new cart product entry for product {}", requestDto.getProductId());

        return validateStockAndQuantity(requestDto.getProductId(), requestDto.getQuantity(), productInfo.getStock())
                .then(Mono.defer(() -> { // mono.defer to delay execution for validation
                    CartProduct newCartProduct = buildCartProduct(cartId, requestDto, productInfo);
                    return cartProductsRepository.save(newCartProduct);
                }))
                .doOnSuccess(savedProduct ->
                        log.debug("Created new cart product {} with quantity {}", savedProduct.getProductId(), savedProduct.getQuantity()));
    }

    // ==================== VALIDATIONS ====================

    private Mono<Cart> validateCartOwnership(Cart cart, String userUuid) {

        if (!userUuid.equals(cart.getUserUuid())) {
            log.warn("User {} attempted to access cart {} owned by {}", userUuid, cart.getId(), cart.getUserUuid());
            return Mono.error(new UnauthorizedCartAccessException("Cart with Id: " + cart.getId() + " does not belong to the user"));
        }
        log.debug("Cart {} ownership validated for user {}", cart.getId(), userUuid);
        return Mono.just(cart);
    }

    private Mono<Cart> validateCartIsActive(Cart cart) {

        if (cart.getStatus() != CartStatus.ACTIVE) {
            log.warn("Attempt to modify non-active cart {}. Status: {}", cart.getId(), cart.getStatus());
            return Mono.error(new CartNotActiveException("Cart with Id: " + cart.getId() + " is not active. Status: " + cart.getStatus()));
        }
        return Mono.just(cart);
    }

    private Mono<ProductDetailsDto> validateProductAvailability(ProductDetailsDto productInfo) {

        if (!productInfo.getActive()) {
            log.warn("Product {} is inactive", productInfo.getId());
            return Mono.error(new ProductNotAvailableException("Product with id: " + productInfo.getId() + " is inactive"));
        }
        return Mono.just(productInfo);
    }

    private Mono<Void> validateStockAndQuantity(Long productId, Integer requestedQuantity, Integer availableStock) {

        if (availableStock < requestedQuantity) {
            log.warn("Not enough stock for product {}: requested {}, available {}", productId, requestedQuantity, availableStock);
            return Mono.error(new NotEnoughStockException("Not enough stock for product id: " + productId));
        }
        if (requestedQuantity > maxQuantityPerProduct) {
            log.warn("Max quantity exceeded for product {}: {}", productId, requestedQuantity);
            return Mono.error(new MaxQuantityExceededException("Cannot add more than 99 units to cart"));
        }
        return Mono.empty();
    }

    // ==================== UPDATE CART ====================
    // Update cart totals after adding product and save.

    private Mono<Cart> updateCartTotals(Cart cart,
                                        AddProductToCartRequestDto requestDto,
                                        CartProduct cartProduct) {

        log.debug("Updating cart {} totals (Adding products)", cart.getId());

        cart.setTotalProducts(cart.getTotalProducts() + requestDto.getQuantity());
        BigDecimal priceToAdd = cartProduct.getProductPrice()
                .multiply(BigDecimal.valueOf(requestDto.getQuantity()));
        cart.setTotalPrice(cart.getTotalPrice().add(priceToAdd));
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    private Mono<Cart> updateCartTotalsDelete(Cart cart, CartProduct cartProduct) {

        int newTotalProducts = cart.getTotalProducts() - cartProduct.getQuantity();
        BigDecimal newTotalPrice = cart.getTotalPrice()
                .subtract(cartProduct.getProductPrice()
                        .multiply(BigDecimal.valueOf(cartProduct.getQuantity())));

        if (cart.getTotalProducts() < cartProduct.getQuantity()) {
            log.error("Cart {} corrupted: totalProducts {} < quantity to remove {}",
                    cart.getId(), cart.getTotalProducts(), cartProduct.getQuantity());
            return Mono.error(new IllegalStateException(
                    "Cart data inconsistent. Cannot remove " + cartProduct.getQuantity() +
                            " items from cart with " + cart.getTotalProducts() + " total items."));
        }

        if (newTotalProducts < 0 || newTotalPrice.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Cart {} would have negative totals: products={}, price={}",
                    cart.getId(), newTotalProducts, newTotalPrice);
            return Mono.error(new IllegalStateException(
                    "Cart data inconsistent. Operation would result in negative totals."));
        }

        cart.setTotalProducts(newTotalProducts);
        cart.setTotalPrice(newTotalPrice);
        cart.setUpdatedAt(LocalDateTime.now());

        return cartRepository.save(cart);
    }

    // ==================== BUILDERS ====================
    // Build response DTO after successful addition.

    private ResponseProductToCartDto buildAddProductResponse(Cart savedCart, CartProduct cartProduct) {

        return ResponseProductToCartDto.builder()
                .id(cartProduct.getProductId())
                .name(cartProduct.getProductName())
                .price(cartProduct.getProductPrice())
                .cartId(savedCart.getId())
                .cartTotalItems(savedCart.getTotalProducts())
                .cartTotalPrice(savedCart.getTotalPrice())
                .message(cartProduct.getQuantity() + " Ud " + cartProduct.getProductName() +
                        " added to cart with id: " + savedCart.getId() + " successfully")
                .build();
    }

    private RemoveProductsFromCartResponseDto buildRemoveProductResponse(Cart cart, CartProduct product){

        return RemoveProductsFromCartResponseDto.builder()
                .message("Removed " + product.getQuantity() + " unit(s) of " + product.getProductName() + " from cart")
                .cartId(cart.getId())
                .productId(product.getProductId())
                .cartName(cart.getName())
                .productName(product.getProductName())
                .cartStatus(CartStatus.ACTIVE)
                .cartTotalItems(cart.getTotalProducts())
                .cartTotalPrice(cart.getTotalPrice())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();

    }

    private RemoveProductsFromCartResponseDto buildProductNotExistsInCartResponse(Long cartId, Long productId){

        return RemoveProductsFromCartResponseDto.builder()
                .message("Product not in cart or already removed")
                .cartId(cartId)
                .productId(productId)
                .build();

    }

    private CartProduct buildCartProduct(Long cartId,
                                         AddProductToCartRequestDto requestDto,
                                         ProductDetailsDto productInfo) {
        return CartProduct.builder()
                .cartId(cartId)
                .productId(productInfo.getId())
                .quantity(requestDto.getQuantity())
                .productName(productInfo.getName())
                .productPrice(productInfo.getPrice())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}