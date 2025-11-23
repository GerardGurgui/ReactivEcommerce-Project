package Ecommerce.Reactive.MyData_service.service;

import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICartService {

    Mono<CartDto> getCartById(Long idCart);

    Flux<CartDto> getAllCarts();

    Mono<CartDto> createCartForUser(CartDto cartDto);
}