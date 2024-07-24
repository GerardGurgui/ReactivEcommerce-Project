package Ecommerce.Reactive.UserAuthentication_service.service.usermanagement;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.UserNotFoundException;
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

    public UserManagementConnectorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(USERMANAGEMENT_URL).build();
    }


    public Mono<UserLoginDto> getUserByEmail(String email){

        LOGGER.info("Request to UserManagementConnectorService: getUserByEmail with email: " + email);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getUserLoginByEmail")
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new UserNotFoundException("Error during GET request to getUserByEmail: User not found for email: " + email)))
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

    public Mono<UserLoginDto> getUserByUsername(String username){

        LOGGER.info("Request to UserManagementConnectorService: getUserByUsername with username: " + username);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getUserLoginByUsername")
                        .queryParam("username", username) //username o userName?
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new UserNotFoundException("Error during GET request to getUserByUsername: User not found for username: " + username)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new ServerErrorException("Error during GET request to getUserByUsername: Server error",
                                new RuntimeException("Server error"))))
                .bodyToMono(UserLoginDto.class)
                .doOnNext(user -> LOGGER.info("Response From: getUserByUsername: User obtained: " + user))
                .onErrorMap(UserNotFoundException.class, ex -> {
                    LOGGER.info("Response From: getUserByUsername: User not found");
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", ex);
                })
                .onErrorMap(ServerErrorException.class, ex -> {
                    LOGGER.info("Response From: getUserByUsername: Server error");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", ex);
                });
    }

}
