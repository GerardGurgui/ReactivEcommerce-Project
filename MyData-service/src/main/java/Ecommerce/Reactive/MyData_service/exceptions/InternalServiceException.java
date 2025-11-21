package Ecommerce.Reactive.MyData_service.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InternalServiceException extends RuntimeException {

    private final LocalDateTime timestamp;

    public InternalServiceException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return this.timestamp.format(formatter);
    }

    @Override
    public String toString() {
        return super.toString() + " at " + getFormattedTimestamp();
    }
}
