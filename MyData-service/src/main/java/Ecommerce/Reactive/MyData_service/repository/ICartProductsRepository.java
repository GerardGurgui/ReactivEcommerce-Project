package Ecommerce.Reactive.MyData_service.repository;

import Ecommerce.Reactive.MyData_service.entity.CartProducts;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartProductsRepository extends ReactiveCrudRepository<CartProducts, Long>{
}
