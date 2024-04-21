package Ecommerce.Reactive.MyData_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

// tabla intermedia entre cart y product para manejar la relacion muchos a muchos
//faltan validations! o en dtos??

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("cart_products")
public class CartProducts {

    @Id
    private Long id;

    @Column("cart_id")
    private Long cartId;

    @Column("product_id")
    private Long productId;

    @Column("products_quantity")
    private int productsQuantity;
}
