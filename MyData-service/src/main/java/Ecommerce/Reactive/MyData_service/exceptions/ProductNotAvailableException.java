package Ecommerce.Reactive.MyData_service.exceptions;

import java.time.LocalDateTime;

public class ProductNotAvailableException extends RuntimeException{

    private final LocalDateTime timestamp;

    public ProductNotAvailableException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }


}
