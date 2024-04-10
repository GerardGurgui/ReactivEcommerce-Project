package Ecommerce.usermanagement.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDto {

    private String message;
    private LocalDate date;

}
