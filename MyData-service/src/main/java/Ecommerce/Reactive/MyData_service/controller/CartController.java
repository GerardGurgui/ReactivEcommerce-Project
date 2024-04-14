package Ecommerce.Reactive.MyData_service.controller;

import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.entity.Product;
import Ecommerce.Reactive.MyData_service.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/MyData/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    @PostMapping("/createCart/{userUuid}")
    public ResponseEntity<Mono<Cart>> createCart(@RequestBody Cart cart, @PathVariable String userUuid) {

        return ResponseEntity.ok(cartService.saveCartForUser(cart, userUuid));
    }

    @PostMapping("/addProductToCart/{idProduct}/{idCart}")
    public ResponseEntity<Mono<Cart>> addToCart(@PathVariable Long idProduct, @PathVariable Long idCart) {

        return ResponseEntity.ok(cartService.addProductToCart(idProduct, idCart));

    }

    @GetMapping("/getCart/{idCart}")
    public ResponseEntity<Mono<Cart>> getCart(@PathVariable Long idCart) {

        return ResponseEntity.ok(cartService.getCartById(idCart));
    }


}
