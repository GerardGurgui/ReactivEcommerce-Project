//package Ecommerce.Reactive.ApiGateway_service.config;
//
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
//import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
//import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
//import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.env.Environment;
//
//public class CustomLoadBalancerConfiguration {
//    @Bean
//    ReactorLoadBalancer<ServiceInstance> roundRobinLoadBalancer(Environment environment,
//                                                                LoadBalancerClientFactory loadBalancerClientFactory) {
//
//        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
//        return new RoundRobinLoadBalancer(loadBalancerClientFactory
//                .getLazyProvider(name, ServiceInstanceListSupplier.class),
//                name);
//    }
//}