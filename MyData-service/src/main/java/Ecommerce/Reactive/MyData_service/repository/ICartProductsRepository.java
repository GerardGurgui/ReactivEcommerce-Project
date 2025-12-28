package Ecommerce.Reactive.MyData_service.repository;

import Ecommerce.Reactive.MyData_service.entity.CartProduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ICartProductsRepository extends ReactiveCrudRepository<CartProduct, Long> {

    Mono<CartProduct> findByCartIdAndProductId(Long cartId, Long productId);

    Flux<CartProduct> findAllByCartId(Long cartId);

    Flux<CartProduct> getAllProductsByCartId(Long cartId);

//    Flux<CartProduct> getAllProductsByCartName(String cartName);
}
