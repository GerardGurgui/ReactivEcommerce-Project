package Ecommerce.Reactive.MyData_service.repository;

import Ecommerce.Reactive.MyData_service.entity.Cart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface ICartRepository extends ReactiveCrudRepository<Cart, Long> {

    Mono<Cart> findCartByName(String cartName);

    Mono<Boolean> existsCartByName(String cartName);

    Flux<Cart> getAllCartsByUserUuid(String userUuid);

}
