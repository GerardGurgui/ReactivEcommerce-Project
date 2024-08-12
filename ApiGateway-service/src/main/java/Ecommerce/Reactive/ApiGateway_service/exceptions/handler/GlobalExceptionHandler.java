package Ecommerce.Reactive.ApiGateway_service.exceptions.handler;

import Ecommerce.Reactive.ApiGateway_service.exceptions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public Mono<Void> handleUnauthorizedException(ServerWebExchange exchange, UnauthorizedException ex) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());

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

    @ExceptionHandler(InvalidAuthorizationHeaderException.class)
    public Mono<Void> handleInvalidAuthorizationHeaderException(ServerWebExchange exchange, InvalidAuthorizationHeaderException ex) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());

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

    @ExceptionHandler(MissingAuthorizationHeaderException.class)
    public Mono<Void> handleMissingAuthorizationHeaderException(ServerWebExchange exchange, MissingAuthorizationHeaderException ex) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());

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

    @ExceptionHandler(TokenExpiredException.class)
    public Mono<Void> handleTokenExpiredException(ServerWebExchange exchange, TokenExpiredException ex) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());

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
