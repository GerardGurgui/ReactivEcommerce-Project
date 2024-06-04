package Ecommerce.Reactive.MyData_service.service.userManagement;
import Ecommerce.Reactive.MyData_service.DTO.UserCartDto;
import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import Ecommerce.Reactive.MyData_service.exceptions.CartNotFoundException;
import Ecommerce.Reactive.MyData_service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

/*
* Service class for communicating with microservice usermanagement
* */

@Service
public class UserManagementConnectorService {


    private final static Logger LOGGER = Logger.getLogger(UserManagementConnectorService.class.getName());

    private final WebClient webClient;
//    private final String USERMANAGEMENT_URL_LB;
    private final String USERMANAGEMENT_URL = "http://localhost:8085/api/usermanagement";


//    CONSTRUCTOR CON EUREKA Y LOAD BALANCER
//    public UserManagementConnectorService(WebClient.Builder webClientBuilder,
//                                          @Value("${UserManagement.service.url}") String url) {
//        this.USERMANAGEMENT_URL_LB = url;
//        this.webClient = webClientBuilder.baseUrl(USERMANAGEMENT_URL_LB).build();
//    }

    //CONSTRUCTOR SIN EUREKA
    public UserManagementConnectorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(USERMANAGEMENT_URL).build();
    }

    //comprobacion uuid en cartservice

    //POSIBLE METODO CON flux.merge, PARA OBTENER PRODUCTOS Y CARRITOS DE 2 DIFERENTES MICROSERVICIOS
    //PUEDE QUE EN OTRA CLASE NO AQUÍ, ANALIZAR BIEN

    public Mono<UserDto> getUserByUuidBasic(String uuid) {

        LOGGER.info("UserManagementConnectorService: getUserByUuidBasic: " + webClient.get()
                .uri("/getUserBasic/{uuid}", uuid)
                .retrieve()
                .bodyToMono(UserDto.class));

        return webClient.get()
                .uri("/getUserBasic/{uuid}", uuid)
                .retrieve()
                //si llega un 4xx error o 5xx desde el otro servicio lo capturamos y lanzamos una excepcion
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse ->
                                Mono.error(new UserNotFoundException("User not found during GET request to getUserByUuidBasic with UUID: " + uuid)))


                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse ->
                                Mono.error(new ServerErrorException("Error during GET request to getUserByUuidBasic with UUID: " +uuid,
                                           new RuntimeException("Error during GET request to getUserByUuidBasic with UUID: " + uuid))))

                .bodyToMono(UserDto.class)
                .onErrorMap(UserNotFoundException.class, ex -> {
                    LOGGER.info("User not found during GET request to getUserByUuidBasic with UUID: " + uuid);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
                })
                .onErrorMap(ServerErrorException.class, ex -> {
                    LOGGER.info("Error during GET request to getUserByUuidBasic with UUID: " + uuid);
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
                });

    }


    public Mono<Void> updateUserHasCart(UserCartDto userCartDto) {

        LOGGER.info("Initiating PUT request to updateUserHasCart with UUID: {" +
                userCartDto.getCartDto().getUserUuid() + "}");


        return webClient.put()
                .uri("/updateUserHasCart/")
                .body(Mono.just(userCartDto), UserCartDto.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse ->
                                Mono.error(new CartNotFoundException("Cart not found during PUT request to updateUserHasCart with UUID: "
                                        + userCartDto.getCartDto().getUserUuid())))


                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse ->
                                Mono.error(new ServerErrorException("Error during PUT request to updateUserHasCart with UUID: "
                                        + userCartDto.getCartDto().getUserUuid(),
                                        new RuntimeException("Error during PUT request to updateUserHasCart with UUID: "
                                        + userCartDto.getCartDto().getUserUuid()))))

                .bodyToMono(Void.class)
                .onErrorMap(CartNotFoundException.class, ex -> {
                    LOGGER.info("Cart not found during PUT request to updateUserHasCart with UUID: " + userCartDto.getCartDto().getUserUuid());
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
                })
                .onErrorMap(ServerErrorException.class, ex -> {
                    LOGGER.info("Error during PUT request to updateUserHasCart with UUID:" + userCartDto.getCartDto().getUserUuid());
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage(),ex);
                });

    }




}
