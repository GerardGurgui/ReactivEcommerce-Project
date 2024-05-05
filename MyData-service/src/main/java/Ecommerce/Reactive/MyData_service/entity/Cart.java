package Ecommerce.Reactive.MyData_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("carts")
public class Cart {

    //falta mejorar e implementar bien atributos, metodos y calculos
    // eliminar productos, calcular total, ordenar productos, etc
    //faltan validations! o en dtos??

    @Id
    private Long id;

    @Column("user_uuid")
    private String userUuid;

    private String name;

    @Column("total_products")
    private int totalProducts;

    @Column("total_price")
    private double totalPrice;

    private String status;

    //que mas atributos deberia tener un carrito??

    //falta lista productos


}
