package Ecommerce.Reactive.MyData_service.exceptions.handler;


import Ecommerce.Reactive.MyData_service.exceptions.CartNotFoundException;
import Ecommerce.Reactive.MyData_service.exceptions.ResourceNullException;
import Ecommerce.usermanagement.exceptions.UserNotFoundException;
import Ecommerce.usermanagement.exceptions.error.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@ControllerAdvice
public class GlobalExceptionsHandler{


    @ExceptionHandler(UserNotFoundException.class)
    public Mono<Void> handleUserNotFoundException(ServerWebExchange exchange, UserNotFoundException exception) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), exception.getFormattedTimestamp());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return exchange.getResponse().writeWith(
                Mono.just(
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(objectMapper.writeValueAsBytes(errorResponse))
                )
        );
    }

    @ExceptionHandler(CartNotFoundException.class)
    public Mono<Void> handleCartNotFoundException(ServerWebExchange exchange, CartNotFoundException exception) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return exchange.getResponse().writeWith(
                Mono.just(
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(objectMapper.writeValueAsBytes(errorResponse))
                )
        );
    }

    @ExceptionHandler(ResourceNullException.class)
    public Mono<Void> handleResourceNullException(ServerWebExchange exchange, ResourceNullException exception) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), exception.getFormattedTimestamp());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return exchange.getResponse().writeWith(
                Mono.just(
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(objectMapper.writeValueAsBytes(errorResponse))
                )
        );
    }




}
