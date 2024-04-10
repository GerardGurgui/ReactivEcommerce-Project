package Ecommerce.usermanagement.exceptions;

public class UserNotFoundException extends RuntimeException {

    private final String username;

    public UserNotFoundException(String message, String username) {
        super(message);
        this.username = username;
    }
}
