package Ecommerce.usermanagement.exceptions;

public class EmailNotFoundException extends RuntimeException{

    private final String email;

    public EmailNotFoundException(String message, String email) {
        super(message);
        this.email = email;
    }
}
