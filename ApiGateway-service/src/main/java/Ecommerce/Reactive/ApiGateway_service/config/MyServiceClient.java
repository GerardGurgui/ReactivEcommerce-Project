package Ecommerce.Reactive.ApiGateway_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MyServiceClient {

    @Autowired
    private ReactorLoadBalancerExchangeFilterFunction lbFunction;

//    @Value("${usermanagement.service.name}")
//    private String userManagementServiceName;

    public Mono<String> callUserManagementService() {
        return WebClient.builder()
                .baseUrl("http://USERMANAGEMENT-SERVICE")
                .filter(lbFunction)
                .build()
                .get()
                .uri("/api/usermanagement/**")
                .retrieve()
                .bodyToMono(String.class);
    }
}