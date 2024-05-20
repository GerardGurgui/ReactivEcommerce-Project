package Ecommerce.Reactive.MyData_service.repository;

import Ecommerce.Reactive.MyData_service.entity.Cart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Repository
public interface ICartRepository extends ReactiveCrudRepository<Cart, Long> {

    Optional<String> findCartByName(String cartName);

    Boolean existsCartByName(String cartName);

    //pendiente de implementar, es una opcion pero no se si es la mejor y faltaria los productos dentro los carritos
    Flux<Cart> getAllCartsByUserUuid(String userUuid);

}
