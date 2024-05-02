//package Ecommerce.Reactive.ApiGateway_service.config;
//
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
//import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//@LoadBalancerClients({@LoadBalancerClient(value = "UserManagement-service", configuration = CustomLoadBalancerConfiguration.class),
//        @LoadBalancerClient(value = "ApiGateway-service", configuration = CustomLoadBalancerConfiguration.class),
//        @LoadBalancerClient(value = "MyData-service", configuration = CustomLoadBalancerConfiguration.class)})
//
//public class LoadBalancerConfiguration {
//
//    @Bean
//    @LoadBalanced
//    public WebClient.Builder loadBalancedWebClientBuilder() {
//        return WebClient.builder();
//    }
//}