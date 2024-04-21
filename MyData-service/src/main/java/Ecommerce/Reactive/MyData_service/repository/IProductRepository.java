package Ecommerce.Reactive.MyData_service.repository;

import Ecommerce.Reactive.MyData_service.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IProductRepository extends ReactiveCrudRepository<Product, Long> {

    Mono<Product> findByName(String name);

}
