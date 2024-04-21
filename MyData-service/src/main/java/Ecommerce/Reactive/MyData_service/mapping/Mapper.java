package Ecommerce.Reactive.MyData_service.mapping;

import Ecommerce.Reactive.MyData_service.DTO.CartDto;
import Ecommerce.Reactive.MyData_service.DTO.ProductDTO;
import Ecommerce.Reactive.MyData_service.entity.Cart;
import Ecommerce.Reactive.MyData_service.entity.Product;
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

    @Override
    public Mono<Product> ProductDtoToProduct(ProductDTO productDTO) {

        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        return Mono.just(product);

    }


    @Override
    public Mono<ProductDTO> mapProductToProductDto(Product product) {

        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);
        return Mono.just(productDTO);
    }


}
