package Ecommerce.Reactive.UserAuthentication_service.config;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.userGateway.UserGateway;
import Ecommerce.Reactive.UserAuthentication_service.domain.usecase.LoginUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {


    @Bean
    public LoginUseCase loginUseCase(UserGateway userGateway) {
        return new LoginUseCase(userGateway);
    }

}
