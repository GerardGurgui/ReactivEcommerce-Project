package Ecommerce.Reactive.MyData_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {

    //faltan validations! o en dtos??

    @Id
    private Long id;

    private String name;

    private String description;

    private double price;

    private String category;

//    private String image;


    //que mas atributos deberia tener un producto??

}
