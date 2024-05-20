package Ecommerce.Reactive.MyData_service.entity;

import Ecommerce.Reactive.MyData_service.DTO.CartStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.util.Iterator;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("carts")
public class Cart {

    //falta mejorar e implementar bien atributos, metodos y calculos
    // eliminar productos, calcular total, ordenar productos, etc

    @Id
    private Long id;

    @Column("user_uuid")
    private String userUuid;

    @NotNull
    private String name;

    @Column("total_products")
    private int totalProducts;

    @Column("total_price")
    private double totalPrice;

    @Column("status")
    private CartStatus status;

    //que mas atributos deberia tener un carrito??

    //falta lista productos


}
