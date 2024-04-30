package Ecommerce.Reactive.MyData_service.service.userManagement;
import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import Ecommerce.Reactive.MyData_service.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

/*
* Service class for communicating with microservice usermanagement
* */

@Service
public class UserManagementConnectorService {


    private final static Logger LOGGER = Logger.getLogger(UserManagementConnectorService.class.getName());

    private final WebClient webClient;
    private static final String USERMANAGEMENT_URL = "http://localhost:8085/api/usermanagement";


    public UserManagementConnectorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(USERMANAGEMENT_URL).build();
    }


    public Mono<UserDto> getUserByUuidBasic(String uuid) {

        LOGGER.info("UserManagementConnectorService: getUserByUuidBasic: " + webClient.get()
                .uri("/getUserBasic/{uuid}", uuid)
                .retrieve()
                .bodyToMono(UserDto.class));

        return webClient.get()
                .uri("/getUserBasic/{uuid}", uuid)
                .retrieve()
                //si llega un 4xx error desde el otro servicio lo capturamos aquí
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse ->  Mono.error(new UserNotFoundException(
                        "Error during GET request to getUserByUuidBasic with UUID: " + uuid)))

                .bodyToMono(UserDto.class)
                .onErrorMap(UserNotFoundException.class, ex -> {
                    LOGGER.info("Error during GET request to getUserByUuidBasic with UUID: " + uuid);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
                });

    }


    public Mono<Void> updateUserHasCart(String uuid){


        LOGGER.info("Initiating PUT request to updateUserHasCart with UUID: {" +
                "uuid" + "}");

        //faltaria comprobar algun error en la respuesta relacionado con los carritos

        return webClient.put()
                .uri("/updateUserHasCart/{uuid}", uuid)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> LOGGER.info("Completed PUT request to updateUserHasCart with UUID: " + uuid))
                .doOnError(e -> LOGGER.info("Error during PUT request to updateUserHasCart with UUID: " + uuid));
    }




}
