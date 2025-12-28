package Ecommerce.Reactive.MyData_service.service;
import Ecommerce.Reactive.MyData_service.DTO.carts.CartDto;
import Ecommerce.Reactive.MyData_service.DTO.carts.CartStatus;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.exceptions.*;
import Ecommerce.Reactive.MyData_service.mapping.IConverter;
import Ecommerce.Reactive.MyData_service.repository.ICartProductsRepository;
import Ecommerce.Reactive.MyData_service.repository.ICartRepository;
import Ecommerce.Reactive.MyData_service.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class CartService {

    private final static Logger LOGGER = Logger.getLogger(CartService.class.getName());

    private final ICartRepository cartRepository;
    private final ICartProductsRepository cartProductsRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    private IConverter converter;

    @Autowired
    public CartService(ICartRepository cartRepository,
                       ICartProductsRepository cartProductsRepository,
                       SecurityUtils securityUtils){
        this.cartRepository = cartRepository;
        this.cartProductsRepository = cartProductsRepository;
        this.securityUtils = securityUtils;
    }

    // UTILITY METHODS
    private Mono<CartDto> getAllProductsFromCartAndBuildCartDto(Cart cart){

        return cartProductsRepository.findAllByCartId(cart.getId())
                .collectList()
                .map(products -> CartDto.builder()
                        .userUuid(cart.getUserUuid())
                        .name(cart.getName())
                        .status(cart.getStatus())
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

    public Mono<CartDto> createCartForUser(CartDto cartDto){

        return securityUtils.extractUserUuidFromJwt()
                .switchIfEmpty(Mono.error(new ResourceNullException("User UUID not found in JWT token. Ensure the token is valid and is the same user.")))
                .flatMap(userUuid -> verifyCartNameDoesNotExist(cartDto.getName())
                        .then(Mono.just(userUuid)))
                .doOnNext(userUuid -> cartDto.setUserUuid(userUuid))
                .flatMap(saveCart -> saveCartToDb(cartDto))
                .flatMap(converter::cartToDto);
    }

    private Mono<Boolean> verifyCartNameDoesNotExist(String cartName) {

        return cartRepository.existsCartByName(cartName)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new CartNameAlreadyExistsException("Cart name already exists: " + cartName));
                    }
                    return Mono.just(true);
                });
    }

    private Mono<Cart> saveCartToDb(CartDto cartDto){

        return converter.cartDtoToCart(cartDto)
                .map(cart -> {
                    cart.setTotalProducts(0);
                    cart.setTotalPrice(0.0);
                    cart.setStatus(CartStatus.ACTIVE);
                    cart.setCreatedAt(LocalDateTime.now());
                    return cart;
                })
                .flatMap(cart -> cartRepository.save(cart))
                .switchIfEmpty(Mono.error(new InternalServiceException("Failed to save cart to database")));
    }


}
