//package Ecommerce.Reactive.ApiGateway_service.config;
//
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.logging.Logger;
//
//@Component
//public class LoggingFilter implements GlobalFilter, Ordered {
//
//    private static final Logger logger = Logger.getLogger(LoggingFilter.class.getName());
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        logger.info("Path: " + exchange.getRequest().getPath());
//        System.out.println("------Global Pre Filter executed");
//        return chain.filter(exchange)
//                .then(Mono.fromRunnable(() -> {
//                    logger.info("Response code: " + exchange.getResponse().getStatusCode());
//                    System.out.println("--------Global Post Filter executed");
//                }));
//    }
//
//    @Override
//    public int getOrder() {
//        return -1;
//    }
//}