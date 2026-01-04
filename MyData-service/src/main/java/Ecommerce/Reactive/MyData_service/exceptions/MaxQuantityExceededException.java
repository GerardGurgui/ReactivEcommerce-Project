package Ecommerce.Reactive.MyData_service.exceptions;

public class MaxQuantityExceededException extends RuntimeException{

    public MaxQuantityExceededException(String message) {
        super(message);
    }

}
