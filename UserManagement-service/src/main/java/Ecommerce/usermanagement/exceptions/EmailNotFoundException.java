package Ecommerce.usermanagement.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class EmailNotFoundException extends RuntimeException{

    private final String email;

    private final LocalDateTime timestamp;

    public EmailNotFoundException(String message, String email) {
        super(message);
        this.email = email;
        this.timestamp = LocalDateTime.now();
    }

    public EmailNotFoundException(String message) {
        super(message);
        this.email = null;
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
