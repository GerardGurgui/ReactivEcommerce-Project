package Ecommerce.Reactive.MyData_service.controller;

import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.entity.Cart;
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
    public ResponseEntity<Mono<Cart>> createCart(@RequestBody CartDto cartDto, @PathVariable String userUuid) {

        return new ResponseEntity<>(cartService.saveCartForUser(cartDto, userUuid), HttpStatus.CREATED);
    }


    @GetMapping("/getCart/{idCart}")
    public ResponseEntity<Mono<Cart>> getCart(@PathVariable Long idCart) {

        return ResponseEntity.ok(cartService.getCartById(idCart));
    }


}
