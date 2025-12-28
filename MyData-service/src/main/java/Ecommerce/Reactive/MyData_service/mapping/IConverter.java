package Ecommerce.Reactive.MyData_service.mapping;


import Ecommerce.Reactive.MyData_service.DTO.carts.CartDto;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import reactor.core.publisher.Mono;

public interface IConverter {

    //Mono para la entrada y la salida por que es un flujo de datos y no sabemos cuando se va a terminar
    Mono<Cart> cartDtoToCart(CartDto cartDto);

    Mono<CartDto> cartToDto(Cart cart);

}
