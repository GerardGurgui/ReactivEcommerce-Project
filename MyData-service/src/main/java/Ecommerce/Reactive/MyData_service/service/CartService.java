package Ecommerce.Reactive.MyData_service.service;
import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.DTO.UserCartDto;
import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.exceptions.CartNotFoundException;
import Ecommerce.Reactive.MyData_service.exceptions.UserNotFoundException;
import Ecommerce.Reactive.MyData_service.mapping.IConverter;
import Ecommerce.Reactive.MyData_service.repository.ICartRepository;
import Ecommerce.Reactive.MyData_service.service.userManagement.UserManagementConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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


    //--->CHECKS

    private void checkUserUuid(String userUuid) {

        if (userUuid == null || userUuid.isEmpty()) {
            LOGGER.info("UserUuid is null or empty");
            Mono.error(new UserNotFoundException("UserUuid is null or empty", userUuid));
        }
    }

    private Mono<Void> checkIdCart(Long idCart){

        if (idCart == null){
            LOGGER.info("this id cart is null");
            Mono.error(new CartNotFoundException("This id cart is null"));
        }

        return Mono.empty();
    }

    private void checkCartDto(CartDto cartDto) {

        if (cartDto == null) {
            LOGGER.info("CartDto is null");
            Mono.error(new CartNotFoundException("CartDto is null"));
        }

        //falta comprobaciones de propiedades del carrito, nombre, precio, productos etc
    }

    private void checkCart(Cart cart) {

        if (cart == null){
            LOGGER.info("Cart is null");
            Mono.error(new CartNotFoundException("Cart is null"));
        }

    }

    private void checkIfCartNameExists(String name, String userUuid) {
    }

    //--->CRUD

    public Mono<CartDto> createCartForUser(CartDto cartDto, String userUuid) {

        //falta comprobaciones,propiedades del cart correctas, algo mas
        //datos erroneos, seguridad, autorizacion del mismo user etc
        //puede tener varios carritos, pero con nombres diferentes y id por supuesto

        /*obtener usuario por uuid del microservicio de userManagement con webclient
        comprobacion de que el usuario existe (en usermanagement)
        si existe, guardar el carrito con el id del usuario
        y cambiar el estado de usuario a active cart
        devolver el carrito guardado como CartDto*/

        checkUserUuid(userUuid);
        checkCartDto(cartDto);

        return userMngConnectorService.getUserByUuidBasic(userUuid)
                .flatMap(userDto -> userDto.addCart(cartDto))
                .flatMap(userDto-> updateActiveCartForUserAndSaveCart(userDto, cartDto))
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found Uuid: " + userUuid)));

    }


    public Mono<CartDto> updateActiveCartForUserAndSaveCart(UserDto userDto, CartDto cartDto){

        return converter.cartDtoToCart(cartDto)
                .map(cartSetUuid -> {
                    cartSetUuid.setUserUuid(userDto.getUuid());
                    return cartSetUuid;
                })
                .flatMap(cartToSave -> cartRepository.save(cartToSave))
                .doOnNext(setIdCartToUser -> userDto.setCartId(setIdCartToUser.getId()))
                .flatMap(savedCart -> {
                    UserCartDto userCartDto = new UserCartDto(userDto, cartDto);
                    return userMngConnectorService.updateUserHasCart(userCartDto)
                            .then(Mono.just(savedCart));
                })
                .flatMap(cartToDto -> converter.cartToDto(cartToDto));
    }




    ////PENDIENTE, SIGUENTE PASO AQUI-----
    //COMPROBACIONES DE QUE EL CARRITO EXISTE

    public Mono<CartDto> getCartById(Long idCart) {

        checkIdCart(idCart);

        return cartRepository.findById(idCart)
                .doOnNext(cart -> checkCart(cart))
                .flatMap(cartToDto -> converter.cartToDto(cartToDto))
        .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found")));
    }



}
