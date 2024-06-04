package Ecommerce.Reactive.UserAuthentication_service.domain.model.userGateway;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import reactor.core.publisher.Mono;

//gateway para aislar la capa de dominio de la capa de infraestructura
public interface UserGateway {

    Mono<TokenDto> login(UserLoginDto userLoginDto);
}
