package Ecommerce.Reactive.MyData_service.service;
import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.DTO.CartLinkUserDto;
import Ecommerce.Reactive.MyData_service.DTO.CartStatus;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.exceptions.CartNameAlreadyExistsException;
import Ecommerce.Reactive.MyData_service.exceptions.CartNotFoundException;
import Ecommerce.Reactive.MyData_service.exceptions.InternalServiceException;
import Ecommerce.Reactive.MyData_service.exceptions.ResourceNullException;
import Ecommerce.Reactive.MyData_service.mapping.IConverter;
import Ecommerce.Reactive.MyData_service.repository.ICartRepository;
import Ecommerce.Reactive.MyData_service.security.SecurityUtils;
import Ecommerce.Reactive.MyData_service.service.userManagement.UserManagementConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class CartServiceImpl implements ICartService {

    private final static Logger LOGGER = Logger.getLogger(CartServiceImpl.class.getName());

    private final ICartRepository cartRepository;
    private final UserManagementConnectorService userMngConnectorService;
    private final SecurityUtils securityUtils;

    @Autowired
    private IConverter converter;

    @Autowired
    public CartServiceImpl(ICartRepository cartRepository,
                           UserManagementConnectorService userMngConnectorService,
                           SecurityUtils securityUtils){
        this.cartRepository = cartRepository;
        this.userMngConnectorService = userMngConnectorService;
        this.securityUtils = securityUtils;
    }

    //--------->CRUD METHODS<-----------

    public Mono<CartDto> getCartById(Long idCart) {

        return cartRepository.findById(idCart)
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found by ID: " + idCart)))
                .flatMap(cart -> converter.cartToDto(cart));

    }

    public Flux<CartDto> getAllCartsByUserUuid(String userUuid) {

        return cartRepository.getAllCartsByUserUuid(userUuid)
                .switchIfEmpty(Mono.error(new CartNotFoundException("Carts not found for userUuid: " + userUuid)))
                .flatMap(cart -> converter.cartToDto(cart));

    }


    public Mono<CartDto> createCartForUser(CartDto cartDto){

        LOGGER.info("----> Creating cart for user");

        return securityUtils.extractUserUuid()
                .switchIfEmpty(Mono.error(new ResourceNullException("Failed to extract uuid from token")))
                .flatMap(userUuid -> verifyCartNameDoesNotExist(cartDto.getName())
                        .then(Mono.just(userUuid)))
                .doOnNext(userUuid -> cartDto.setUserUuid(userUuid))
                .flatMap(saveCart -> saveCartToDb(cartDto))
                    .flatMap(this::notifyUserManagementServiceCartCreated)
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
                    cart.setStatus(CartStatus.ACTIVE);
                    cart.setCreatedAt(LocalDateTime.now());
                    return cart;
                })
                .flatMap(cart -> cartRepository.save(cart))
                .switchIfEmpty(Mono.error(new InternalServiceException("Failed to save cart to database")));
    }

    private Mono<Cart> notifyUserManagementServiceCartCreated(Cart cart){

        CartLinkUserDto linkDto = new CartLinkUserDto(
                cart.getId(),
                cart.getUserUuid()
        );
        return userMngConnectorService.linkCartToUser(linkDto)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(120)))
                .thenReturn(cart);
    }


}
