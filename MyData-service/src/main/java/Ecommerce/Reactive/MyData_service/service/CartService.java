package Ecommerce.Reactive.MyData_service.service;
import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.DTO.UserCartDto;
import Ecommerce.Reactive.MyData_service.DTO.UserDto;
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



    private void checkIfCartNameExists(String name, String userUuid) {
    }

    //--->CRUD

    //FALTA VALIDAR QUE NO TENGA NOMBRE CARRITO REPETIDO!!!

    //ARREGLAR TIMESTAMP

    public Mono<CartDto> createCartForUser(CartDto cartDto, String userUuid) {

        //falta comprobaciones,propiedades del cart correctas, algo mas
        //datos erroneos, seguridad, autorizacion del mismo user etc
        //puede tener varios carritos, pero con nombres diferentes y id por supuesto

        /*obtener usuario por uuid del microservicio de userManagement con webclient
        comprobacion de que el usuario existe (en usermanagement)
        si existe, guardar el carrito con el id del usuario
        y cambiar el estado de usuario a active cart
        devolver el carrito guardado como CartDto*/

        if (userUuid == null || userUuid.isEmpty()){
            return Mono.error(new ResourceNullException("UserUuid is null or empty"));
        }

        return userMngConnectorService.getUserByUuidBasic(userUuid)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found Uuid: " + userUuid)))
                .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting user by Uuid: " + userUuid + ", User not found")))
                .flatMap(userDto -> userDto.addCart(cartDto))
                .flatMap(userDto-> updateActiveCartForUserAndSaveCart(userDto, cartDto));
    }

    //FALTA MODIFICAR EL ESTADO DE LOS CARRITOS DE USUARIO
    //---> SEGUIR AQUI: MODIFICAR ORDEN DE LA CADENA, PRIMERO COMPROBAR ERRORES, SI TODO OK , ENTONCES ADELANTE
    //---> MODIFICAR TODOS LOS METODOS EL ORDEN DE SECUENCIA DE OPERACIONES, PRIMER EMPTY O ERROR, DESPUES OPERACIONES

    public Mono<CartDto> updateActiveCartForUserAndSaveCart(UserDto userDto, CartDto cartDto){

        return converter.cartDtoToCart(cartDto)
                .map(cartSetUuid -> {
                    cartSetUuid.setUserUuid(userDto.getUuid());
                    return cartSetUuid;
                })
                .flatMap(cartToSave -> cartRepository.save(cartToSave))
                .flatMap(savedCart -> {
                    UserCartDto userCartDto = new UserCartDto(userDto, cartDto);
                    return userMngConnectorService.updateUserHasCart(userCartDto)
                            .then(Mono.just(savedCart));
                })
                .flatMap(cartToDto -> converter.cartToDto(cartToDto));
    }


    ////--->GET

    public Mono<CartDto> getCartById(Long idCart) {

        return cartRepository.findById(idCart)
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found by ID: " + idCart)))
                .onErrorResume(e -> Mono.error(new CartNotFoundException("Error getting cart by ID: " + idCart + ", Cart not found")))
                .flatMap(cart -> converter.cartToDto(cart));

    }

    //error bad sql grammar

    public Flux<CartDto> getAllCartsByUserUuid(String userUuid) {

//        checkUserUuid(userUuid);

        return cartRepository.getAllCartsByUserUuid(userUuid)
                .flatMap(cart -> converter.cartToDto(cart))
                .switchIfEmpty(Mono.error(new CartNotFoundException("Carts not found for userUuid: " + userUuid)));

    }


}
