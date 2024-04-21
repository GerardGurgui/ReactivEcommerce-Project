package Ecommerce.Reactive.MyData_service.service;

import Ecommerce.Reactive.MyData_service.entity.CartProducts;
import Ecommerce.Reactive.MyData_service.repository.ICartProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CartProductService {

    private final ICartProductsRepository cartProductRepository;

    @Autowired
    public CartProductService(ICartProductsRepository cartProductRepository) {
        this.cartProductRepository = cartProductRepository;
    }


    public Mono<CartProducts> addCartProduct(CartProducts cartProducts) {
        return cartProductRepository.save(cartProducts);
    }
}
