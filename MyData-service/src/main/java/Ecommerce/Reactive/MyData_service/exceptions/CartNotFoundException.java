package Ecommerce.Reactive.MyData_service.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class CartNotFoundException extends RuntimeException{

    private final LocalDateTime timestamp;

    public CartNotFoundException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }

//    public String getFormattedTimestamp() {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//        return this.timestamp.format(formatter);
//    }
//
//    @Override
//    public String toString() {
//        return super.toString() + " at " + getFormattedTimestamp();
//    }
}