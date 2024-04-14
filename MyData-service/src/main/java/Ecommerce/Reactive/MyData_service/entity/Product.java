package Ecommerce.Reactive.MyData_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.GeneratedValue;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {

    @Id
    @GeneratedValue
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("price")
    private double price;

    @Column("category")
    private String category;

    @Column("image")
    private String image;


}
