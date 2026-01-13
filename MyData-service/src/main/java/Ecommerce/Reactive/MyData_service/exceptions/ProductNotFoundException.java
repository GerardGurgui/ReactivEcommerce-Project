package Ecommerce.Reactive.MyData_service.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ProductNotFoundException extends RuntimeException {

    private final Long productId;
    private final LocalDateTime timestamp;

    public ProductNotFoundException(String message, Long productId) {
        super(message);
        this.productId = productId;
        this.timestamp = LocalDateTime.now();
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return this.timestamp.format(formatter);
    }
}
