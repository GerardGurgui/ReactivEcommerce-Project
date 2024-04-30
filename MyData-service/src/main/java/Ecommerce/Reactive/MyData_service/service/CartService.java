package Ecommerce.Reactive.MyData_service.service;
import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.exceptions.CartNotFoundException;
import Ecommerce.Reactive.MyData_service.exceptions.UserNotFoundException;
import Ecommerce.Reactive.MyData_service.mapping.IMapper;
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
    private IMapper mapper;

    @Autowired
    public CartService(ICartRepository cartRepository,
                       UserManagementConnectorService userMngConnectorService) {
        this.cartRepository = cartRepository;
        this.userMngConnectorService = userMngConnectorService;
    }

    private void checkUserUuid(String userUuid) {

        if (userUuid == null || userUuid.isEmpty()) {
            LOGGER.info("UserUuid is null or empty");
            Mono.error(new UserNotFoundException("UserUuid is null or empty", userUuid));
        }
    }


    public Mono<Cart> saveCartForUser(CartDto cartDto, String userUuid) {

        //falta comprobaciones, userUuid existe, propiedades del cart correctas, algo mas
        //datos erroneos, seguridad, autorizacion del mismo user etc
        //puede tener varios carritos, pero con nombres diferentes y id por supuesto
        // si no encuentra usuario arreglar la exception.

        checkUserUuid(userUuid);

        if (cartDto == null) {
            LOGGER.info("CartDto is null");
            return Mono.error(new CartNotFoundException("CartDto is null"));
        }

        return userMngConnectorService.getUserByUuidBasic(userUuid)
                .flatMap(userDto -> mapper.cartDtoToCart(cartDto)
                        .doOnNext(cartToSetUuid -> cartToSetUuid.setUserUuid(userDto.getUuid()))
                        .flatMap(cartToSave -> cartRepository.save(cartToSave))
                            .flatMap(savedCart -> userMngConnectorService.updateUserHasCart(userUuid) //actualizar user con cart
                                 .then(Mono.just(savedCart))));
    }


//    public Mono<Cart> addProductToCart(Long idProduct, Long idCart) {
//
//        //buscar que exista el producto
//
//        return cartRepository.findById(idCart)
//                .flatMap(cart -> getProductById(idProduct)
//                        .flatMap(product -> {
//                            cart.addProduct(product);
//                            return cartRepository.save(cart);
//                        }));
//    }

    public Mono<Cart> getCartById(Long idCart) {

        return cartRepository.findById(idCart);
    }

}
