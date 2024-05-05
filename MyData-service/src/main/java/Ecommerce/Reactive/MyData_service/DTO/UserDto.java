package Ecommerce.Reactive.MyData_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private String uuid;
    private String username;
    private String email;
    private Long cartId;
    private List<CartDto> carts;


    public Mono<UserDto> addCart(CartDto cartDto) {

        if (carts == null) {
            carts = new ArrayList<>();
        }
        carts.add(cartDto);
        return Mono.just(this);
    }
}