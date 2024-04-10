package Ecommerce.usermanagement.exceptions;

import lombok.Getter;

@Getter
public class EmailExistsException extends RuntimeException {

    private final String email;

    public EmailExistsException(String message, String email) {
        super(message);
        this.email = email;
    }
}