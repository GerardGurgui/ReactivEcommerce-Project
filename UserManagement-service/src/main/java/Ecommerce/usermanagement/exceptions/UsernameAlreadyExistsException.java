package Ecommerce.usermanagement.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class UsernameAlreadyExistsException extends RuntimeException{

    private final String username;

    private final LocalDateTime timestamp;

    public UsernameAlreadyExistsException(String message, String username) {
        super(message);
        this.username = username;
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
