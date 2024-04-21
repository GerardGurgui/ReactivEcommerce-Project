package Ecommerce.Reactive.MyData_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {

    private String user_uuid;
    private String name;
    private int total_products;
    private double total_price;

}
