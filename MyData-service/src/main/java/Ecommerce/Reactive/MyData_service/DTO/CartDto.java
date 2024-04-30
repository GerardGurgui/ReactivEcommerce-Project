package Ecommerce.Reactive.MyData_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {

    private String userUuid;
    private String name;
    private int totalProducts;
    private double totalPrice;

}
