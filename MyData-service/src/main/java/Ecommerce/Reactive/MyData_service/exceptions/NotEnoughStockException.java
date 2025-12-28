package Ecommerce.Reactive.MyData_service.exceptions;

import java.time.LocalDateTime;

public class NotEnoughStockException extends RuntimeException{

    private final LocalDateTime timestamp;

    public NotEnoughStockException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }

}
