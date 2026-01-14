package Ecommerce.Reactive.MyData_service.exceptions.handler;


import Ecommerce.Reactive.MyData_service.exceptions.*;
import Ecommerce.usermanagement.exceptions.UserNotFoundException;
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


    @ExceptionHandler(CartNameAlreadyExistsException.class)
    public Mono<Void> handleCartNameAlreadyExistsException(ServerWebExchange exchange, CartNameAlreadyExistsException exception) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.CONFLICT);
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

    @ExceptionHandler(ProductNotFoundException.class)
    public Mono<Void> handleCartNameAlreadyExistsException(ServerWebExchange exchange, ProductNotFoundException exception) throws JsonProcessingException {

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

    @ExceptionHandler(MaxQuantityExceededException.class)
    public Mono<Void> handleCartNameAlreadyExistsException(ServerWebExchange exchange, MaxQuantityExceededException exception) throws JsonProcessingException {

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

    @ExceptionHandler(NotEnoughStockException.class)
    public Mono<Void> handleNotEnoughStockException(ServerWebExchange exchange, NotEnoughStockException exception) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(ProductNotAvailableException.class)
    public Mono<Void> handleProductNotAvailableException(ServerWebExchange exchange, ProductNotAvailableException exception) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(UnauthorizedCartAccessException.class)
    public Mono<Void> handleUnauthorizedCartAccessException(ServerWebExchange exchange, UnauthorizedCartAccessException exception) throws JsonProcessingException {

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
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




}
