package Ecommerce.Reactive.MyData_service.entity;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("cart_products")
public class CartProduct {

    @Id
    private Long id;

    @Version
    private Long version;

    @Column("cart_id")
    private Long cartId;

    @Column("product_id")
    private Long productId;

    @Column("product_name")
    private String productName;

    private Integer quantity;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("product_price")
    private BigDecimal productPrice;
}
