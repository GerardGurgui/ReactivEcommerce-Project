package Ecommerce.Reactive.MyData_service.service;
import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.DTO.CartStatus;
import Ecommerce.Reactive.MyData_service.DTO.UserCartDto;
import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import Ecommerce.Reactive.MyData_service.exceptions.CartNameAlreadyExistsException;
import Ecommerce.Reactive.MyData_service.exceptions.CartNotFoundException;
import Ecommerce.Reactive.MyData_service.exceptions.ResourceNullException;
import Ecommerce.Reactive.MyData_service.exceptions.UserNotFoundException;
import Ecommerce.Reactive.MyData_service.mapping.IConverter;
import Ecommerce.Reactive.MyData_service.repository.ICartRepository;
import Ecommerce.Reactive.MyData_service.service.jwt.JwtTokenService;
import Ecommerce.Reactive.MyData_service.service.userManagement.UserManagementConnectorService;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Service
public class CartService {

    private final static Logger LOGGER = Logger.getLogger(CartService.class.getName());
    private final ICartRepository cartRepository;
    private final UserManagementConnectorService userMngConnectorService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    private IConverter converter;

    @Autowired
    public CartService(ICartRepository cartRepository,
                       UserManagementConnectorService userMngConnectorService,
                       JwtTokenService jwtTokenService) {
        this.cartRepository = cartRepository;
        this.userMngConnectorService = userMngConnectorService;
        this.jwtTokenService = jwtTokenService;
    }

    //--------->CRUD
    ////--->GET

    public Mono<CartDto> getCartById(Long idCart) {

        return cartRepository.findById(idCart)
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found by ID: " + idCart)))
                .flatMap(cart -> converter.cartToDto(cart));

    }

    //falta comprobaciones,propiedades del cart correctas, algo mas

    /*obtener usuario por uuid del microservicio de userManagement con webclient
    comprobacion de que el usuario existe (en usermanagement)
    si existe, guardar el carrito con el id del usuario
    realizar modificaciones pertinentes sobre el carrito
    devolver el carrito guardado como CartDto*/

    public Mono<CartDto> createCartForUser(CartDto cartDto) {

        LOGGER.info("----> Creating cart for user");

        return jwtTokenService.extractUserUuidFromToken()
                .switchIfEmpty(Mono.error(new ResourceNullException("UserUuid is empty or null")))

                .flatMap(this::verifyUserExists)
                .doOnSuccess(userDto -> LOGGER.info("----> User found: " + userDto.getUuid()))

                .flatMap(userDto -> verifyCartNameDoesNotExist(cartDto.getName())
                        .then(Mono.just(userDto)))

                .flatMap(userDto -> setCartProperties(userDto, cartDto))
                .flatMap(userDto -> userDto.addCartDto(cartDto))
                .flatMap(userDto -> updateActiveCartForUserAndSaveCart(userDto, cartDto))

                .onErrorResume(this::handleError)
                .doOnNext(cartDto1 -> LOGGER.info("----> Cart created: " + cartDto1.getName()));

    }

    private Mono<UserDto> verifyUserExists(String userUuid) {

        return userMngConnectorService.getUserByUuidBasic(userUuid)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found, Uuid: " + userUuid)));
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


    private Mono<UserDto> setCartProperties(UserDto userDto, CartDto cartDto) {

        //AÑADIR PROPIEDADES DEL CARRITO NECESARIAS, FALTAN PROPIEDADES
        cartDto.setUserUuid(userDto.getUuid());
        cartDto.setStatus(CartStatus.ACTIVE);
        return Mono.just(userDto);
    }

    //---> SEGUIR AQUI: MODIFICAR ORDEN DE LA CADENA, PRIMERO COMPROBAR ERRORES, SI TO_DO OK , ENTONCES ADELANTE
    //---> MODIFICAR TODOS LOS METODOS EL ORDEN DE SECUENCIA DE OPERACIONES, PRIMER EMPTY O ERROR, DESPUES OPERACIONES

    public Mono<CartDto> updateActiveCartForUserAndSaveCart(UserDto userDto, CartDto cartDto){

        return converter.cartDtoToCart(cartDto)
                .flatMap(savedCart -> {
                    UserCartDto userCartDto = new UserCartDto(userDto, cartDto);
                    return userMngConnectorService.updateUserHasCart(userCartDto)
                            .then(Mono.just(savedCart));
                })
                .flatMap(cartToSave -> cartRepository.save(cartToSave))
                .flatMap(cartToDto -> converter.cartToDto(cartToDto));
    }

    private Mono<CartDto> handleError(Throwable error) {

        if (error instanceof ResourceNullException) {

            LOGGER.warning("----> Failed to create cart: " + error.getMessage());
            return Mono.error(new ResourceNullException("Failed to create cart: " + error.getMessage()));

        } else if (error instanceof UserNotFoundException) {

            LOGGER.warning("----> Failed to create cart: " + error.getMessage());
            return Mono.error(new UserNotFoundException("Failed to create cart: " + error.getMessage()));

        } else if (error instanceof CartNameAlreadyExistsException) {

            LOGGER.warning("----> Failed to create cart: " + error.getMessage());
            return Mono.error(new CartNameAlreadyExistsException("Failed to create cart: " + error.getMessage()));
        }
        LOGGER.warning("----> Failed to create cart: " + error.getMessage());
        return Mono.error(new RuntimeException("Failed to create cart: " + error.getMessage()));
    }

    //error bad sql grammar

    public Flux<CartDto> getAllCartsByUserUuid(String userUuid) {

//        checkUserUuid(userUuid);

        return cartRepository.getAllCartsByUserUuid(userUuid)
                .switchIfEmpty(Mono.error(new CartNotFoundException("Carts not found for userUuid: " + userUuid)))
                .flatMap(cart -> converter.cartToDto(cart));

    }


}
