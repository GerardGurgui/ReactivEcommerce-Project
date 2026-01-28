package Ecommerce.usermanagement.exceptions.handler;

import Ecommerce.usermanagement.exceptions.*;
import Ecommerce.usermanagement.exceptions.errordetails.ErrorResponse;
import Ecommerce.usermanagement.exceptions.errordetails.ValidationErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolationException;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionsHandler{

    private final ObjectMapper objectMapper;

    // Centralized Map: Exception → HttpStatus
    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_STATUS_MAP = Map.of(
            UsernameAlreadyExistsException.class, HttpStatus.CONFLICT,
            EmailAlreadyExistsException.class, HttpStatus.CONFLICT,
            EmailNotFoundException.class, HttpStatus.NOT_FOUND,
            UserNotFoundException.class, HttpStatus.NOT_FOUND,
            ServerErrorException.class, HttpStatus.INTERNAL_SERVER_ERROR
    );


    public GlobalExceptionsHandler (ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

    }

    // ==========================================
    // VALIDATION HANDLERS
    // ==========================================

    /**
     * Handle validation errors from @Valid on @RequestBody
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Void> handleValidationException(ServerWebExchange exchange, WebExchangeBindException ex) {

        try {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            // Extraer errores: field → message
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );

            ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                    "Validation failed",
                    errors,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
            );

            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);

            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
            );
        } catch (JsonProcessingException e) {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * Handle constraint violations from @Validated
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<Void> handleConstraintViolation(
            ServerWebExchange exchange,
            ConstraintViolationException ex) {
        try {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> errors = new HashMap<>();
            ex.getConstraintViolations().forEach(violation -> {
                String propertyPath = violation.getPropertyPath().toString();
                String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
                errors.put(fieldName, violation.getMessage());
            });

            ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                    "Validation failed",
                    errors,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
            );

            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);

            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
            );
        } catch (JsonProcessingException e) {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * Builds a JSON error response for exceptions
     */
    private Mono<Void> buildErrorResponse(ServerWebExchange exchange,
                                          RuntimeException exception,
                                          HttpStatus status)
            throws JsonProcessingException {

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String timestamp = extractTimestamp(exception);

        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                timestamp
        );

        byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);

        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }
    /**
     * Extracts formatted timestamp from exception if available
     */
    private String extractTimestamp(RuntimeException exception) {

        try {
            var method = exception.getClass().getMethod("getFormattedTimestamp");
            return (String) method.invoke(exception);
        } catch (Exception e) {
            return LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            );
        }
    }

    /**
     * Generic handler for mapped exceptions
     */
    private Mono<Void> handleMappedException(ServerWebExchange exchange, RuntimeException exception)
            throws JsonProcessingException {

        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(
                exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR
        );
        return buildErrorResponse(exchange, exception, status);
    }

   @ExceptionHandler(
           {UserNotFoundException.class,
           EmailNotFoundException.class,
            UsernameAlreadyExistsException.class,
            EmailAlreadyExistsException.class,
            ServerErrorException.class})
    public Mono<Void> handleCustomExceptions(ServerWebExchange exchange, RuntimeException exception){
       try {
           return handleMappedException(exchange, exception);
       } catch (JsonProcessingException e) {
           exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
           return exchange.getResponse().setComplete();
       }}
}
