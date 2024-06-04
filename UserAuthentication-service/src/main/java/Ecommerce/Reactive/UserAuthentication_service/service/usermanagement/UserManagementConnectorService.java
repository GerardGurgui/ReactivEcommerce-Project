package Ecommerce.Reactive.UserAuthentication_service.service.usermanagement;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.util.UriBuilder;
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


    public Mono<UserLoginDto> getUserByUsernameOrEmail(String username, String email) {

        LOGGER.info("Request to UserManagementConnectorService: getUserByEmailOrUsername: email: " + email + " username: " + username);

        return webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/getUserByUsernameOrEmail");
                    if (username != null) {
                        builder = builder.queryParam("username", username);
                    }
                    if (email != null) {
                        builder = builder.queryParam("email", email);
                    }
                    return builder.build();
                })
                .retrieve()
                //si llega un 4xx error o 5xx desde el otro servicio lo capturamos y lanzamos una excepcion
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse ->
                        Mono.error(new UserNotFoundException("Error during GET request to getUserByEmailOrUsername with Username: "
                                + username + " and Email: " + email + ", User not found")))

                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse ->
                        Mono.error(new ServerErrorException("Error during GET request to getUserByEmailOrUsername with Username: " + username,
                                   new RuntimeException("Error during GET request to getUserByEmailOrUsername with Username: " + username))))

                .bodyToMono(UserLoginDto.class)
                .doOnNext(user -> LOGGER.info("Response From: getUserByEmailOrUsername: user get with exit: " + user))
                .onErrorMap(UserNotFoundException.class, ex -> {
                    LOGGER.info("Response From: getUserByEmailOrUsername: user not found");
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", ex);
                })
                .onErrorMap(ServerErrorException.class, ex -> {
                    LOGGER.info("Response From: getUserByEmailOrUsername: server error");
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", ex);
                });

    }




}
