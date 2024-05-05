package Ecommerce.Reactive.MyData_service.controller;

import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import Ecommerce.Reactive.MyData_service.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/MyData/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @PostMapping("/createCart/{userUuid}")
    public ResponseEntity<Mono<CartDto>> createCart(@RequestBody CartDto cartDto, @PathVariable String userUuid) {

        return new ResponseEntity<>(cartService.createCartForUser(cartDto, userUuid), HttpStatus.CREATED);
    }


    @GetMapping("/getCart/{idCart}")
    public ResponseEntity<Mono<CartDto>> getCart(@PathVariable Long idCart) {

        return new ResponseEntity<>(cartService.getCartById(idCart), HttpStatus.FOUND);
    }


}
