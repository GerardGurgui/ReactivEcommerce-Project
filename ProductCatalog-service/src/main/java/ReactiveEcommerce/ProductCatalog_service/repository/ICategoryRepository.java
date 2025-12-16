package ReactiveEcommerce.ProductCatalog_service.repository;

import ReactiveEcommerce.ProductCatalog_service.entity.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ICategoryRepository extends ReactiveCrudRepository<Category, Long>{

    Mono<Category> findByNameIgnoreCase(String name);

    Mono<Category> findByid(Long id);

}
