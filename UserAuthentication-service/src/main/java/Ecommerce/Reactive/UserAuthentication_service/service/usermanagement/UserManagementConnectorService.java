package Ecommerce.Reactive.UserAuthentication_service.service.usermanagement;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.login.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.UserCreatedResponseDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.UserRegisterInternalDto;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.EmailAlreadyExistsException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.UserNotFoundException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.UsernameAlreadyExistsException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.errorDetails.ErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Service
public class UserManagementConnectorService {

    private final static Logger LOGGER = Logger.getLogger(UserManagementConnectorService.class.getName());

    private final WebClient webClient;
    private final String USERMANAGEMENT_URL = "http://localhost:8085/api/usermanagement";

    @Value("${internal.api-key}")
    private String internalApiKey;

    public UserManagementConnectorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(USERMANAGEMENT_URL).build();
    }

    // Login calls

    public Mono<UserLoginDto> getUserLoginByEmail(String email){

        LOGGER.info("Request to UserManagementConnectorService: getUserByEmail with email: " + email);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/getUserLoginByEmail")
                        .queryParam("email", email)
                        .build())
                .header("X-Internal-API-Key", internalApiKey)
                .retrieve()
                .onStatus(status -> status.value() == 401,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")))
                .onStatus(status -> status.value() == 403,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden")))
                .onStatus(status -> status.value() == 404,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new ServerErrorException("Error during GET request to getUserByEmail: Server error",
                                new RuntimeException("Server error"))))
                .bodyToMono(UserLoginDto.class)
                .doOnNext(user -> LOGGER.info("Response From: getUserByEmail: User obtained: " + user))
                .onErrorMap(UserNotFoundException.class, ex -> {
                    LOGGER.info("Response From: getUserByEmail: User not found");
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", ex);
                })
                .onErrorMap(ServerErrorException.class, ex -> {
                    LOGGER.info("Response From: getUserByEmail: Server error");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", ex);
                });
    }

    public Mono<UserLoginDto> getUserLoginByUsername(String username){

        LOGGER.info("Request to UserManagementConnectorService: getUserByUsername with username: " + username);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/getUserLoginByUsername")
                        .queryParam("username", username)
                        .build())
                .header("X-Internal-API-Key", internalApiKey)
                .retrieve()
                .onStatus(status -> status.value() == 401,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")))
                .onStatus(status -> status.value() == 403,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden")))
                .onStatus(status -> status.value() == 404,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client error: " + body))))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error: " + body))))
                .bodyToMono(UserLoginDto.class)
                .doOnNext(user -> LOGGER.info("Response From: getUserByUsername: User obtained: " + user))
                .onErrorMap(throwable -> {
                    if (throwable instanceof ResponseStatusException) return throwable;
                    LOGGER.severe("Error contacting user-management: " + throwable.getMessage());
                    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error contacting user-management", throwable);
                });
    }

    // Registration calls
    public Mono<UserCreatedResponseDto> createUser(UserRegisterInternalDto userRegisterInternalDto){

        LOGGER.info("Request to UserManagementConnectorService: sendUserDtoToSignIn with username: " + userRegisterInternalDto.getUsername());

        return webClient.post()
                .uri("/internal/addUser")
                .header("X-Internal-API-Key", internalApiKey)
                .bodyValue(userRegisterInternalDto)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.CONFLICT.value(),
                        response -> response.bodyToMono(ErrorResponse.class)
                                .flatMap(errorResponse -> {

                                    String message = errorResponse.getErrorDetails();

                                    LOGGER.warning("❌ Conflict error: " + message);

                                    if (message.contains("Username")) {
                                        return Mono.error(new UsernameAlreadyExistsException(message));
                                    }
                                    if (message.contains("Email")) {
                                        return Mono.error(new EmailAlreadyExistsException(message));
                                    }
                                    return Mono.error(new RuntimeException(message));
                                })
                )
                .onStatus(status -> status.is4xxClientError() && status.value() != HttpStatus.CONFLICT.value(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    LOGGER.warning("❌ Client error (4xx): " + errorBody);
                                    return Mono.error(new RuntimeException("Client error: " + errorBody));
                                })
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new ServerErrorException("Error during POST request to registerUser: Server error",
                                new RuntimeException("Server error"))))
                .bodyToMono(UserCreatedResponseDto.class)
                .doOnSuccess(response -> LOGGER.info("Response From: sendUserDtoToSignIn: RegistrationResponse obtained: " + response))
                .doOnError( error -> LOGGER.warning("❌ Error From: sendUserDtoToSignIn: " + error.getMessage()));
    }


}
