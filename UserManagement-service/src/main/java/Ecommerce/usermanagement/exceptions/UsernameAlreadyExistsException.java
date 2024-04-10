package Ecommerce.usermanagement.exceptions;

import lombok.Getter;

@Getter
public class UsernameAlreadyExistsException extends RuntimeException{

    private final String username;

    public UsernameAlreadyExistsException(String message, String username) {
        super(message);
        this.username = username;
    }

}
