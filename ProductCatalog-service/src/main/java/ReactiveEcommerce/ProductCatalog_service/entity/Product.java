package ReactiveEcommerce.ProductCatalog_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {

    @Id
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;

    @Column("category_id")
    private Long categoryId;

    private Boolean active;

    @Column("image_url")
    private String imageUrl;

    @Column("created_at")
    private Timestamp createdAt;

    @Column("updated_at")
    private Timestamp updatedAt;

    private Integer stock;

    @Transient
    private Category category;



}
