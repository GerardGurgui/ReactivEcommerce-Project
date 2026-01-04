package Ecommerce.Reactive.MyData_service.controller;

import Ecommerce.Reactive.MyData_service.DTO.cartProducts.AddProductToCartRequestDto;
import Ecommerce.Reactive.MyData_service.DTO.cartProducts.ResponseProductToCartDto;
import Ecommerce.Reactive.MyData_service.DTO.carts.CartDto;
import Ecommerce.Reactive.MyData_service.service.CartProductService;
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


@RestController
@RequestMapping("/api/MyData/cart")
@Validated
public class CartController {

    private final CartService cartService;
    private final CartProductService cartProductService;

    @Autowired
    public CartController(CartService cartService, CartProductService cartProductService) {
        this.cartService = cartService;
        this.cartProductService = cartProductService;
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/createCart")
    public Mono<ResponseEntity<CartDto>> createCart(@Valid @RequestBody CartDto cartDto) {

        return cartService.createCartForUser(cartDto)
                .map(cart -> new ResponseEntity<>(cart, HttpStatus.CREATED));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getCart/{idCart}")
    public Mono<CartDto> getCart(@PathVariable Long idCart) {

        return cartService.getCartById(idCart);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getAllCarts")
    public Flux<CartDto> getAllCarts() {

        return cartService.getAllCarts();
    }

    // ----> CREATE OPERATION FOR ADDING PRODUCTS TO CART <----

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/addProducts/{cartId}/items")
    public Mono<ResponseEntity<ResponseProductToCartDto>> addProductToCart(
            @PathVariable Long cartId,
            @Valid @RequestBody AddProductToCartRequestDto requestDto) {

        return cartProductService.addProductToCart(cartId, requestDto)
                .map(response -> new ResponseEntity<>(response, HttpStatus.CREATED));
    }


}
