package Ecommerce.Reactive.MyData_service.exceptions;

import java.time.LocalDateTime;

public class CartNotActiveException extends RuntimeException {

    private final LocalDateTime timestamp;

    public CartNotActiveException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }
}