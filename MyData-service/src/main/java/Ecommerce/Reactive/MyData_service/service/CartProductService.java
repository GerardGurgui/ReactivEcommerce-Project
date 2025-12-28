package Ecommerce.Reactive.MyData_service.service;

import Ecommerce.Reactive.MyData_service.DTO.cartProducts.AddProductToCartRequestDto;
import Ecommerce.Reactive.MyData_service.DTO.cartProducts.ProductDetailsFromMsDto;
import Ecommerce.Reactive.MyData_service.DTO.cartProducts.ResponseProductToCartDto;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.entity.CartProduct;
import Ecommerce.Reactive.MyData_service.exceptions.*;
import Ecommerce.Reactive.MyData_service.repository.ICartProductsRepository;
import Ecommerce.Reactive.MyData_service.repository.ICartRepository;
import Ecommerce.Reactive.MyData_service.security.SecurityUtils;
import Ecommerce.Reactive.MyData_service.service.communication.productsMs.ProductCatalogConnectorService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class CartProductService {

    private final ICartProductsRepository cartProductsRepository;
    private final ICartRepository cartRepository;
    private final SecurityUtils securityUtils;
    private final ProductCatalogConnectorService productCatalogConnectorService;


    public CartProductService(ICartProductsRepository cartProductsRepository,
                              ICartRepository cartRepository,
                              SecurityUtils securityUtils,
                              ProductCatalogConnectorService productCatalogConnectorService) {
        this.productCatalogConnectorService = productCatalogConnectorService;
        this.cartProductsRepository = cartProductsRepository;
        this.cartRepository = cartRepository;
        this.securityUtils = securityUtils;
    }

    public Mono<ResponseProductToCartDto> addProductToCart(Long cartId, AddProductToCartRequestDto requestDto) {

        return getCartIfOwnedByCurrentUser(cartId)
                    .flatMap(cart -> productCatalogConnectorService.getProductInfoFromProductsMs(requestDto.getProductId())
                    .flatMap(productInfo -> findOrCreateCartProduct(cart,requestDto, productInfo))
                    .flatMap(cartProduct -> updateCartTotalsAndBuildResponse(cart, requestDto, cartProduct))
                    );
    }

    // --> Extract and validate userUuid from JWT, check cart ownership
    private Mono<Cart> getCartIfOwnedByCurrentUser(Long cartId) {

        return securityUtils.extractUserUuidFromJwt()
                .switchIfEmpty(Mono.error(new UserNotFoundException("User UUID not found in JWT token")))
                .flatMap(userUuid -> cartRepository.findById(cartId)
                        .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found with id: " + cartId)))
                        .flatMap(cart -> {
                            if (!userUuid.equals(cart.getUserUuid())) {
                                return Mono.error(new UnauthorizedCartAccessException("Cart with Id: " + cartId + " does not belong to the user"));
                            }
                            return Mono.just(cart);
                        })
                );
    }

    // --> check if product already in cart + validate data (ckeck stock always)
        //--> if exists, update quantity
        // --> if not exists, create new CartProduct
    private Mono<CartProduct> findOrCreateCartProduct(Cart cartToUpdate,
                                                      AddProductToCartRequestDto requestDto,
                                                      ProductDetailsFromMsDto productInfo) {

        return cartProductsRepository.findByCartIdAndProductId(cartToUpdate.getId(), requestDto.getProductId())
                .flatMap(existingProduct -> {
                    // products exists in cart, check stock, availability and if ok, update quantity
                    Integer totalQuantity = existingProduct.getQuantity() + requestDto.getQuantity();

                    if (productInfo.getStock() < totalQuantity){
                        return Mono.error(new NotEnoughStockException("Not enough stock for product id: " + requestDto.getProductId()));
                    }
                    if (!productInfo.getActive()){
                        return Mono.error(new ProductNotAvailableException("Product with id: " + requestDto.getProductId() + " is not available"));
                    }
                    // update quantity and updatedAt
                    existingProduct.setQuantity(totalQuantity);
                    existingProduct.setUpdatedAt(LocalDateTime.now());
                    return cartProductsRepository.save(existingProduct);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // product not in cart, check stock and availability, if ok create new CartProduct
                    if (productInfo.getStock() < requestDto.getQuantity()){
                        return Mono.error(new NotEnoughStockException("Not enough stock for product id: " + requestDto.getProductId()));
                    }
                    if (!productInfo.getActive()){
                        return Mono.error(new ProductNotAvailableException("Product with id: " + requestDto.getProductId() + " is not available"));
                    }
                    CartProduct newCartProduct = buildNewCartProduct(cartToUpdate.getId(), requestDto, productInfo);
                    return cartProductsRepository.save(newCartProduct);
                }));
    }

    // --> update data cart and persist to database, build response dto
    private Mono<ResponseProductToCartDto> updateCartTotalsAndBuildResponse(Cart cartToUpdate,
                                                                            AddProductToCartRequestDto requestDto,
                                                                            CartProduct newProductToCart) {

        cartToUpdate.setTotalProducts(cartToUpdate.getTotalProducts() + requestDto.getQuantity());
        cartToUpdate.setTotalPrice(cartToUpdate.getTotalPrice() + (newProductToCart.getProductPrice() * requestDto.getQuantity()));
        cartToUpdate.setUpdatedAt(LocalDateTime.now());

        return cartRepository.save(cartToUpdate)
                .map(savedCart -> ResponseProductToCartDto.builder()
                        .id(newProductToCart.getProductId())
                        .name(newProductToCart.getProductName())
                        .price(newProductToCart.getProductPrice())
                        .cartId(savedCart.getId())
                        .cartTotalItems(savedCart.getTotalProducts())
                        .cartTotalPrice(savedCart.getTotalPrice())
                        .message(newProductToCart.getQuantity() + " Ud " + newProductToCart.getProductName() +
                                " added to cart with id: " + cartToUpdate.getId() + " successfully ")
                        .build());
    }


    private CartProduct buildNewCartProduct(Long cartId,
                                                   AddProductToCartRequestDto requestDto,
                                                   ProductDetailsFromMsDto productInfo) {
        CartProduct newCartProduct = new CartProduct();
        newCartProduct.setCartId(cartId);
        newCartProduct.setProductId(productInfo.getId());
        newCartProduct.setQuantity(requestDto.getQuantity()); // quantity from user request
        newCartProduct.setProductName(productInfo.getName());
        newCartProduct.setProductPrice(productInfo.getPrice());
        newCartProduct.setCreatedAt(LocalDateTime.now());
        newCartProduct.setUpdatedAt(LocalDateTime.now());
        return newCartProduct;
    }



}
