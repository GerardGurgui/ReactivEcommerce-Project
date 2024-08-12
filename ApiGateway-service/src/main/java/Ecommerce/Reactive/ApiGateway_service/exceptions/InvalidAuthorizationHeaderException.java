package Ecommerce.Reactive.ApiGateway_service.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class InvalidAuthorizationHeaderException extends RuntimeException {


    private final LocalDateTime timestamp;

    public InvalidAuthorizationHeaderException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
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