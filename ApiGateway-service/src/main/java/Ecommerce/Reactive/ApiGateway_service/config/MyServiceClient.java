//package Ecommerce.Reactive.ApiGateway_service.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Component
//public class MyServiceClient {
//
//    @Autowired
//    private ReactorLoadBalancerExchangeFilterFunction lbFunction;
//
//    //nombres microservicios
//    @Value("${UserManagement.service.name}")
//    private String userManagementServiceName;
//
//    @Value("${MyData.service.name}")
//    private String myDataServiceName;
//
//    //rutas microservicios
//    @Value("${UserManagement.service.route}")
//    private String userManagementServiceRoute;
//
//    @Value("${MyData.service.route}")
//    private String myDataServiceRoute;
//
//
//
//    public Mono<String> callUserManagementService() {
//        return WebClient.builder()
//                .baseUrl(userManagementServiceName)
//                .filter(lbFunction)
//                .build()
//                .get()
//                .uri(userManagementServiceRoute)
//                .retrieve()
//                .bodyToMono(String.class);
//    }
//
//
//    public Mono<String> callMyDataService() {
//        return WebClient.builder()
//                .baseUrl(myDataServiceName)
//                .filter(lbFunction)
//                .build()
//                .get()
//                .uri(myDataServiceRoute)
//                .retrieve()
//                .bodyToMono(String.class);
//    }
//}