package Ecommerce.Reactive.MyData_service.DTO;

import lombok.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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