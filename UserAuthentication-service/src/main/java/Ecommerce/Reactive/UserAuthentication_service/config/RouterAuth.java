package Ecommerce.Reactive.UserAuthentication_service.config;

import Ecommerce.Reactive.UserAuthentication_service.controller.LoginController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterAuth {


    private static final String PATH = "auth/";


    @Bean
    public RouterFunction<ServerResponse> route(LoginController loginController) {

        return RouterFunctions.route()
                .POST(PATH + "login", loginController::login)
                .build();
    }



}
