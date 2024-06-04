package Ecommerce.usermanagement.exceptions.handler;

import Ecommerce.usermanagement.exceptions.*;
import Ecommerce.usermanagement.exceptions.errordetails.ErrorResponse;
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


    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public Mono<Void> handleUsernameAlreadyExistsException(ServerWebExchange exchange, UsernameAlreadyExistsException exception) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
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

    @ExceptionHandler(EmailExistsException.class)
    public Mono<Void> handleEmailExistsException(ServerWebExchange exchange, EmailExistsException exception) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
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

    @ExceptionHandler(EmailNotFoundException.class)
    public Mono<Void> handleEmailNotFoundException(ServerWebExchange exchange, EmailNotFoundException exception) throws JsonProcessingException {

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

}
