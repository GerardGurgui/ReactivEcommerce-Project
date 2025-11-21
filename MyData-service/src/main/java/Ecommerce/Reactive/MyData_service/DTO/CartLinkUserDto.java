package Ecommerce.Reactive.MyData_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartLinkUserDto {

    private Long idCart;
    private String userUuid;
}
