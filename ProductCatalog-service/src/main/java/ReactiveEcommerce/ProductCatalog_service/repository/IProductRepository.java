package ReactiveEcommerce.ProductCatalog_service.repository;

import ReactiveEcommerce.ProductCatalog_service.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends ReactiveCrudRepository<Product, Long>{
}
