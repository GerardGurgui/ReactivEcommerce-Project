package Ecommerce.Reactive.MyData_service.service;
import Ecommerce.Reactive.MyData_service.DTO.carts.CartDto;
import Ecommerce.Reactive.MyData_service.DTO.carts.CartStatus;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.exceptions.*;
import Ecommerce.Reactive.MyData_service.mapping.IConverter;
import Ecommerce.Reactive.MyData_service.repository.ICartProductsRepository;
import Ecommerce.Reactive.MyData_service.repository.ICartRepository;
import Ecommerce.Reactive.MyData_service.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Slf4j
@Service
public class CartService {

    private final static Logger LOGGER = Logger.getLogger(CartService.class.getName());

    private final ICartRepository cartRepository;
    private final ICartProductsRepository cartProductsRepository;
    private final SecurityUtils securityUtils;
    private final IConverter converter;

    @Autowired
    public CartService(ICartRepository cartRepository,
                       ICartProductsRepository cartProductsRepository,
                       SecurityUtils securityUtils,
                       IConverter converter) {
        this.cartRepository = cartRepository;
        this.cartProductsRepository = cartProductsRepository;
        this.securityUtils = securityUtils;
        this.converter = converter;
    }

    // UTILITY METHODS
    private Mono<CartDto> getAllProductsFromCartAndBuildCartDto(Cart cart){

        return cartProductsRepository.findAllByCartId(cart.getId())
                .collectList()
                .map(products -> CartDto.builder()
                        .userUuid(cart.getUserUuid())
                        .name(cart.getName())
                        .id(cart.getId())
                        .status(cart.getStatus())
                        .cartTotalItems(cart.getTotalProducts())
                        .cartTotalPrice(cart.getTotalPrice())
                        .createdAt(cart.getCreatedAt())
                        .updatedAt(cart.getUpdatedAt())
                        .products(products)
                        .build());

    }

    private Mono<Cart> validateCartBelongsToUser(Cart cart, String userUuid) {

        if (!cart.getUserUuid().equals(userUuid)) {
            return Mono.error(new UnauthorizedCartAccessException(
                    "Cart with ID: " + cart.getId() + " does not belong to the authenticated user."));
        }
        return Mono.just(cart);
    }

    //--------->CRUD METHODS<-----------
    // --> GETS<--

    public Mono<CartDto> getCartById(Long idCart) {

        return securityUtils.extractUserUuidFromJwt()
                .switchIfEmpty(Mono.error(new ResourceNullException("User UUID not found in JWT token.")))
                .flatMap(userUuid ->  cartRepository.findById(idCart)
                    .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found by ID: " + idCart)))
                        .flatMap(cart -> validateCartBelongsToUser(cart,userUuid))
                            .flatMap(this::getAllProductsFromCartAndBuildCartDto));
    }


    public Flux<CartDto> getAllCarts() {

        return securityUtils.extractUserUuidFromJwt()
                .switchIfEmpty(Mono.error(new ResourceNullException("User UUID not found in JWT token.")))
                .flatMapMany(userUuid -> cartRepository.getAllCartsByUserUuid((userUuid))
                    .switchIfEmpty(Flux.error(new CartNotFoundException("No carts found in the database for this user")))
                        .flatMap(this::getAllProductsFromCartAndBuildCartDto));

    }

    /// --> CREATE CART <--

    public Mono<CartDto> createCartForUser(CartDto cartDto) {

        log.info("Creating cart with name: {}", cartDto.getName());

        return securityUtils.extractUserUuidFromJwt()
                .switchIfEmpty(Mono.error(new ResourceNullException("User UUID not found in JWT token.")))
                .flatMap(userUuid -> verifyCartNameDoesNotExistForUser(cartDto.getName(), userUuid)
                        .thenReturn(userUuid))
                .flatMap(userUuid -> saveCartToDb(cartDto, userUuid))
                .flatMap(converter::cartToDto)
                .doOnSuccess(saved -> log.info("Cart '{}' created successfully", saved.getName()));
    }

    private Mono<Boolean> verifyCartNameDoesNotExistForUser(String cartName, String userUuid) {

        return cartRepository.existsCartByNameAndUserUuid(cartName, userUuid)
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("User {} already has a cart named '{}'", userUuid, cartName);
                        return Mono.error(new CartNameAlreadyExistsException("You already have a cart named: " + cartName));
                    }
                    return Mono.just(true);
                });
    }

    // PENDIENTE CUANDO IMPLEMENTE CHECKOUT CAMBIAR ESTADOS DE LOS CARRITOS

    private Mono<Cart> saveCartToDb(CartDto cartDto, String userUuid) {

        return converter.cartDtoToCart(cartDto)
                .map(cart -> {
                    cart.setUserUuid(userUuid);
                    cart.setTotalProducts(0);
                    cart.setTotalPrice(BigDecimal.valueOf(0.0));
                    cart.setStatus(CartStatus.ACTIVE);
                    cart.setCreatedAt(LocalDateTime.now());
                    return cart;
                })
                .flatMap(cartRepository::save)
                .switchIfEmpty(Mono.error(new InternalServiceException("Failed to save cart in the database.")));
    }
}
