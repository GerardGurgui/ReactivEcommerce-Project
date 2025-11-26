package Ecommerce.Reactive.UserAuthentication_service.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserAccountLockedException extends RuntimeException {

    private final LocalDateTime timestamp;

    public UserAccountLockedException(String message) {
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
