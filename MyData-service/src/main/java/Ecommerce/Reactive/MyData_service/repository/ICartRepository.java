package Ecommerce.Reactive.MyData_service.repository;

import Ecommerce.Reactive.MyData_service.entity.Cart;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartRepository extends R2dbcRepository<Cart, Long> {
}
