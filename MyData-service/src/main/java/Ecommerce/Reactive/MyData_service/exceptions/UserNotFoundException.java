package Ecommerce.Reactive.MyData_service.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final String username;
    private final LocalDateTime timestamp;

    public UserNotFoundException(String message, String username) {
        super(message);
        this.username = username;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.timestamp = LocalDateTime.now();
    }

    public UserNotFoundException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.username = null;
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
