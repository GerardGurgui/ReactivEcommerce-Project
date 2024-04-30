package Ecommerce.Reactive.MyData_service.exceptions;

public class CartNotFoundException extends RuntimeException{

    public CartNotFoundException(String message) {
        super(message);
    }
}
