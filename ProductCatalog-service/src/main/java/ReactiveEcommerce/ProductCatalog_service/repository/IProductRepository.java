package ReactiveEcommerce.ProductCatalog_service.repository;

import ReactiveEcommerce.ProductCatalog_service.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IProductRepository extends ReactiveCrudRepository<Product, Long>{

      /**
     * 1. Listar todos los productos activos
     * USO: Catálogo principal, listar productos disponibles
     **/
    Flux<Product> findByActiveTrue();

    /**
     * 2. Buscar productos por texto (nombre o descripción)
     * USO: Barra de búsqueda - query="laptop" encuentra "Dell Laptop", "Gaming Laptop"
     */
    Flux<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    /**
     * 3. Filtrar productos activos por categoría id y por nombre
     * USO: Filtro de categoría - "Mostrar solo Laptops"
     */
    Flux<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    Flux<Product> findByCategoryNameIgnoreCaseAndActiveTrue(String name);

    /**
     * 4. Filtrar productos activos por rango de precio
     * USO: Filtro de precio - "Entre $100 y $500"
     */
    Flux<Product> findByPriceBetweenAndActiveTrue(Double minPrice, Double maxPrice);

    /**
     * 5. Productos activos ordenados por precio (ascendente)
     * USO: Ordenar "De menor a mayor precio"
     */
    Flux<Product> findByActiveTrueOrderByPriceAsc();

    /**
     * 6. Productos activos ordenados por precio (descendente)
     * USO: Ordenar "De mayor a menor precio"
     */
    Flux<Product> findByActiveTrueOrderByPriceDesc();

    /**
     * 7. Verificar si producto existe y está activo
     * USO: Validar antes de añadir al carrito
     */
    Mono<Boolean> existsByIdAndActiveTrue(Long id);
}
