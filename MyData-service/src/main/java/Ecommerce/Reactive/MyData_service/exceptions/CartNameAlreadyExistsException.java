package Ecommerce.Reactive.MyData_service.exceptions;

public class CartNameAlreadyExistsException extends RuntimeException{

    public CartNameAlreadyExistsException(String message) {
        super(message);
    }

    public CartNameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
