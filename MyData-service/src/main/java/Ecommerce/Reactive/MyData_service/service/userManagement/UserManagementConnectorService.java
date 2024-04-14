package Ecommerce.Reactive.MyData_service.service.userManagement;
import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/*
* Service class for communicating with microservice usermanagement
* */

@Service
public class UserManagementConnectorService {

    private final WebClient webClient;


    public UserManagementConnectorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8085/api/usermanagement").build();
    }

    public Mono<UserDto> getUserByUuidBasic(String uuid) {

        return webClient.get()
                .uri("/getUserBasic/{uuid}", uuid)
                .retrieve()
                .bodyToMono(UserDto.class);
    }


}
