package Ecommerce.Reactive.MyData_service.mapping;


import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.DTO.ProductDTO;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.entity.Product;
import reactor.core.publisher.Mono;

public interface IMapper {

    //Mono para la entrada y la salida por que es un flujo de datos y no sabemos cuando se va a terminar
    Mono<Cart> cartDtoToCart(CartDto cartDto);

    Mono<CartDto> CartToCartDto(Cart cart);

    Mono<Product> ProductDtoToProduct(ProductDTO input);

    Mono<ProductDTO> mapProductToProductDto(Product product);
}
