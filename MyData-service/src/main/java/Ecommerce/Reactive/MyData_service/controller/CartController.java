package Ecommerce.Reactive.MyData_service.controller;

import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import Ecommerce.Reactive.MyData_service.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/MyData/cart")
@Validated
public class CartController {

    private final static Logger LOGGER = Logger.getLogger(CartController.class.getName());

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/createCart")
    public Mono<ResponseEntity<CartDto>> createCart(@Valid @RequestBody CartDto cartDto) {

        return cartService.createCartForUser(cartDto)
                .map(cart -> new ResponseEntity<>(cart, HttpStatus.CREATED));
    }


    @GetMapping("/getCart/{idCart}")
    public ResponseEntity<Mono<CartDto>> getCart(@PathVariable Long idCart) {

        return new ResponseEntity<>(cartService.getCartById(idCart), HttpStatus.FOUND);
    }

    @GetMapping("/getAllCartsFromUserUuid/{userUuid}")
    public ResponseEntity<Flux<CartDto>> getAllCartsFromUserUuid(@PathVariable String userUuid) {

        return new ResponseEntity<>(cartService.getAllCartsByUserUuid(userUuid), HttpStatus.FOUND);
    }


}
