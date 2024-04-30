package Ecommerce.Reactive.MyData_service.mapping;

import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class Mapper implements IMapper {


    @Override
    public Mono<Cart> cartDtoToCart(CartDto cartDto) {

        Cart cart = new Cart();
        BeanUtils.copyProperties(cartDto, cart);
        return Mono.just(cart);
    }

    @Override
    public Mono<CartDto> CartToCartDto(Cart cart) {

        CartDto cartDto = new CartDto();
        BeanUtils.copyProperties(cart, cartDto);
        return Mono.just(cartDto);
    }



}
