package Ecommerce.Reactive.MyData_service.service.userManagement;
import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

/*
* Service class for communicating with microservice usermanagement
* */

@Service
public class UserManagementConnectorService {

    //añadir logs
    private final static Logger LOGGER = Logger.getLogger(UserManagementConnectorService.class.getName());

    private final WebClient webClient;

    public UserManagementConnectorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8085/api/usermanagement").build();
    }


    public Mono<UserDto> getUserByUuidBasic(String uuid) {

        LOGGER.info("UserManagementConnectorService: getUserByUuidBasic: " + webClient.get()
                .uri("/getUserBasic/{uuid}", uuid)
                .retrieve()
                .bodyToMono(UserDto.class));

        return webClient.get()
                .uri("/getUserBasic/{uuid}", uuid)
                .retrieve()
                .bodyToMono(UserDto.class);

    }


    public Mono<Void> updateUserHasCart(String uuid){


        LOGGER.info("Initiating PUT request to updateUserHasCart with UUID: {" +
                "uuid" + "}");

        return webClient.put()
                .uri("/updateUserHasCart/{uuid}", uuid)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> LOGGER.info("Completed PUT request to updateUserHasCart with UUID: {}"))
                .doOnError(e -> LOGGER.info("Error during PUT request to updateUserHasCart with UUID: {}"));
    }




}
