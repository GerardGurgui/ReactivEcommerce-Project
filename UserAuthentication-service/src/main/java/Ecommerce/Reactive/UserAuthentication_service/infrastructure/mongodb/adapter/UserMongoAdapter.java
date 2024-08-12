package Ecommerce.Reactive.UserAuthentication_service.infrastructure.mongodb.adapter;

import Ecommerce.Reactive.UserAuthentication_service.controller.LoginController;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.userGateway.UserGateway;
import Ecommerce.Reactive.UserAuthentication_service.service.jwt.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class UserMongoAdapter implements UserGateway {

    private final Logger logger = Logger.getLogger(LoginController.class.getName());
    private final JwtTokenService authService;

    @Autowired
    public UserMongoAdapter(JwtTokenService authService) {
        this.authService = authService;
    }


    ////FALTARA LIARSE CON KAFKA SI EL LOGIN ES CORRECTO
    //PARA ENVIAR EL MENSAJE DEL EVENTO A USERMANAGEMENT Y REALIZAR LAS MODIFICACIONES ADECUADAS
    //SETLASTESTACCES, ETC
    //PERO ANTEES TEEEST

    //----> FALTARA TAMBIEN IMPLEMENTAR METODOS DE USERDETAILS

    @Override
    public Mono<TokenDto> login(UserLoginDto userLoginDto) {

        return authService.authenticate(userLoginDto);

    }


}
