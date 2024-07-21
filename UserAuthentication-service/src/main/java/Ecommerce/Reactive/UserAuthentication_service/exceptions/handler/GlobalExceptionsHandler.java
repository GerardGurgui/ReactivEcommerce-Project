package Ecommerce.Reactive.UserAuthentication_service.exceptions.handler;

import Ecommerce.Reactive.UserAuthentication_service.exceptions.BadCredentialsException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.EmptyCredentialsException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.UserNotFoundException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.errorDetails.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionsHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<Void> handleBadCredentialsException(ServerWebExchange exchange, BadCredentialsException exception) {

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), exception.getFormattedTimestamp());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            return exchange.getResponse().writeWith(
                    Mono.just(
                            exchange.getResponse()
                                    .bufferFactory()
                                    .wrap(objectMapper.writeValueAsBytes(errorResponse))
                    )
            );
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<Void> handleUserNotFoundException(ServerWebExchange exchange, UserNotFoundException exception) {

        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), exception.getFormattedTimestamp());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            return exchange.getResponse().writeWith(
                    Mono.just(
                            exchange.getResponse()
                                    .bufferFactory()
                                    .wrap(objectMapper.writeValueAsBytes(errorResponse))
                    )
            );
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    @ExceptionHandler(EmptyCredentialsException.class)
    public Mono<Void> handleEmptyCredentialsException(ServerWebExchange exchange, EmptyCredentialsException exception) {

        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), exception.getFormattedTimestamp());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            return exchange.getResponse().writeWith(
                    Mono.just(
                            exchange.getResponse()
                                    .bufferFactory()
                                    .wrap(objectMapper.writeValueAsBytes(errorResponse))
                    )
            );
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }


}




