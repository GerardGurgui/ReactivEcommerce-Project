package Ecommerce.Reactive.MyData_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private String uuid;
    private String username;
    private String email;
    private List<CartDto> carts;


    public Mono<UserDto> addCartDto(CartDto cartDto) {

        if (carts == null) {
            carts = new ArrayList<>();
        }
        carts.add(cartDto);
        return Mono.just(this);
    }
}