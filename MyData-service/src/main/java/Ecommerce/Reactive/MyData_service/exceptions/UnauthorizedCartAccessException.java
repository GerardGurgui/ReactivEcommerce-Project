package Ecommerce.Reactive.MyData_service.exceptions;


import java.time.LocalDateTime;

public class UnauthorizedCartAccessException extends RuntimeException {

    private final LocalDateTime timestamp;

    public UnauthorizedCartAccessException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }
}
