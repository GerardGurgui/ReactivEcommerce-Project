package Ecommerce.Reactive.UserAuthentication_service.domain.usecase;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.userGateway.UserGateway;
import reactor.core.publisher.Mono;

public class LoginUseCase {

    private final UserGateway userGateway;

    public LoginUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public Mono<TokenDto> login(UserLoginDto userLoginDto) {
        return userGateway.login(userLoginDto);
    }
}
