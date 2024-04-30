package Ecommerce.Reactive.MyData_service.exceptions;

public class UserNotFoundException extends RuntimeException {

    private final String username;

    public UserNotFoundException(String message, String username) {
        super(message);
        this.username = username;
    }

    public UserNotFoundException(String message) {
        super(message);
        this.username = null;
    }
}
