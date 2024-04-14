package Ecommerce.Reactive.MyData_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "carts")
public class Cart {

    //falta mejorar e implementar bien atributos, metodos y calculos
    // eliminar productos, calcular total, ordenar productos, etc

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_uuid")
    private String userUuid;
    @Column(name = "cart_name")
    private String name;
    @Column(name = "products_quantity")
    private int quantity;
    @Column(name = "total_price")
    private double total;

    @ManyToMany
    @JoinTable(
        name = "cart_products",
        joinColumns = @JoinColumn(name = "cart_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id"))
    private LinkedHashSet<Product> cartProducts; // LinkedHashSet is used to maintain the order of insertion


    ////-----------------Methods-----------------////

    public void addProduct(Product product){

        if(cartProducts == null){
            cartProducts = new LinkedHashSet<>();
        }
        cartProducts.add(product);
    }


}
