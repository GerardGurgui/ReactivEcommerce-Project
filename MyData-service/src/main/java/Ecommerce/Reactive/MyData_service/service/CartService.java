package Ecommerce.Reactive.MyData_service.service;

import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.entity.Product;
import Ecommerce.Reactive.MyData_service.repository.ICartRepository;
import Ecommerce.Reactive.MyData_service.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CartService {

    @Autowired
    private ICartRepository cartRepository;

    @Autowired
    private IProductRepository productRepository;

    private Mono<Product> getProductById(Long id) {

        return productRepository.findById(id);
    }


    public Mono<Cart> saveCartForUser(Cart cart, String userUuid) {

        cart.setUserUuid(userUuid);

        return cartRepository.save(cart);
    }


    public Mono<Cart> addProductToCart(Long idProduct, Long idCart) {

        //buscar que exista el producto

        return cartRepository.findById(idCart)
                .flatMap(cart -> getProductById(idProduct)
                        .flatMap(product -> {
                            cart.addProduct(product);
                            return cartRepository.save(cart);
                        }));
    }

    public Mono<Cart> getCartById(Long idCart) {

        return cartRepository.findById(idCart);
    }

}
