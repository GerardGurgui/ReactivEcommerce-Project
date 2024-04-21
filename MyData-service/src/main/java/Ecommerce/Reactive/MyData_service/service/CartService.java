package Ecommerce.Reactive.MyData_service.service;

import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.entity.Product;
import Ecommerce.Reactive.MyData_service.mapping.IMapper;
import Ecommerce.Reactive.MyData_service.repository.ICartRepository;
import Ecommerce.Reactive.MyData_service.repository.IProductRepository;
import Ecommerce.Reactive.MyData_service.service.userManagement.UserManagementConnectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CartService {

    private final ICartRepository cartRepository;

    private final IProductRepository productRepository;

    private final UserManagementConnectorService userMngConnectorService;

    @Autowired
    private IMapper mapper;

    @Autowired
    public CartService(ICartRepository cartRepository,
                       IProductRepository productRepository,
                       UserManagementConnectorService userMngConnectorService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userMngConnectorService = userMngConnectorService;
    }

    public Mono<Cart> saveCartForUser(CartDto cartDto, String userUuid) {

        //falta comprobaciones, userUuid existe, propiedades del cart correctas, algo mas
        //comprobar si es null, empty, datos erroneos, seguridad, autorizacion del mismo user etc
        //puede tener varios carritos, pero con nombres diferentes y id por supuesto
        // si no encuentra usuario arreglar la exception.

        return userMngConnectorService.getUserByUuidBasic(userUuid)
                .flatMap(userDto -> mapper.cartDtoToCart(cartDto)
                        .doOnNext(cartToSetUuid -> cartToSetUuid.setUserUuid(userDto.getUuid()))
                        .flatMap(cartToSave -> cartRepository.save(cartToSave))
                        .flatMap(savedCart -> userMngConnectorService.updateUserHasCart(userUuid)
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
