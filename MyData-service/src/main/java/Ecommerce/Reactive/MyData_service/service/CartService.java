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
import Ecommerce.Reactive.MyData_service.service.userManagement.UserManagementConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Service
public class CartService {


    private final static Logger LOGGER = Logger.getLogger(CartService.class.getName());

    private final ICartRepository cartRepository;

    private final UserManagementConnectorService userMngConnectorService;

    @Autowired
    private IConverter converter;

    @Autowired
    public CartService(ICartRepository cartRepository,
                       UserManagementConnectorService userMngConnectorService) {
        this.cartRepository = cartRepository;
        this.userMngConnectorService = userMngConnectorService;
    }

    //--->CRUD

    //falta comprobaciones,propiedades del cart correctas, algo mas
    //datos erroneos, seguridad, autorizacion del mismo user etc
    //puede tener varios carritos, pero con nombres diferentes y id por supuesto

    /*obtener usuario por uuid del microservicio de userManagement con webclient
    comprobacion de que el usuario existe (en usermanagement)
    si existe, guardar el carrito con el id del usuario
    realizar modificaciones pertinentes sobre el carrito
    devolver el carrito guardado como CartDto*/

    public Mono<CartDto> createCartForUser(CartDto cartDto, String userUuid) {

        if (userUuid == null || userUuid.isEmpty()){
            return Mono.error(new ResourceNullException("UserUuid is null or empty"));
        }

        return userMngConnectorService.getUserByUuidBasic(userUuid)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found, Uuid: " + userUuid)))
                .flatMap(userDto -> {
                    if(cartRepository.existsCartByName(cartDto.getName())){
                        return Mono.error(new CartNameAlreadyExistsException("Cart name already exists"));
                    }
                    return Mono.just(userDto);
                })
                .onErrorResume(e -> Mono.error(new CartNameAlreadyExistsException("Cart name: " + cartDto.getName() + ", already exists")))
                .flatMap(userDto -> setCartProperties(userDto, cartDto))
                .flatMap(userDto -> userDto.addCartDto(cartDto))
                .flatMap(userDto-> updateActiveCartForUserAndSaveCart(userDto, cartDto));
    }

    private Mono<UserDto> setCartProperties(UserDto userDto, CartDto cartDto) {

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


    ////--->GET

    public Mono<CartDto> getCartById(Long idCart) {

        return cartRepository.findById(idCart)
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found by ID: " + idCart)))
                .flatMap(cart -> converter.cartToDto(cart));

    }

    //error bad sql grammar

    public Flux<CartDto> getAllCartsByUserUuid(String userUuid) {

//        checkUserUuid(userUuid);

        return cartRepository.getAllCartsByUserUuid(userUuid)
                .switchIfEmpty(Mono.error(new CartNotFoundException("Carts not found for userUuid: " + userUuid)))
                .flatMap(cart -> converter.cartToDto(cart));

    }


}
