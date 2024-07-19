package Ecommerce.Reactive.UserAuthentication_service.infrastructure.mongodb.adapter;

import Ecommerce.Reactive.UserAuthentication_service.controller.LoginController;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.UserLoginDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.TokenDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.userGateway.UserGateway;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.BadCredentialsException;
import Ecommerce.Reactive.UserAuthentication_service.security.JwtProvider;
import Ecommerce.Reactive.UserAuthentication_service.security.userdetails.UserDetailsImpl;
import Ecommerce.Reactive.UserAuthentication_service.service.usermanagement.UserManagementConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class UserMongoAdapter implements UserGateway {

    private final Logger logger = Logger.getLogger(LoginController.class.getName());

    private final UserManagementConnectorService userMngConnector;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserMongoAdapter(UserManagementConnectorService userMngConnector,
                            PasswordEncoder passwordEncoder,
                            JwtProvider jwtProvider) {
        this.userMngConnector = userMngConnector;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }


    ////FALTARA LIARSE CON KAFKA SI EL LOGIN ES CORRECTO
    //PARA ENVIAR EL MENSAJE DEL EVENTO A USERMANAGEMENT Y REALIZAR LAS MODIFICACIONES ADECUADAS
    //SETLASTESTACCES, ETC
    //PERO ANTEES TEEEST

    //----> FALTARA TAMBIEN IMPLEMENTAR METODOS DE USERDETAILS

    @Override
    public Mono<TokenDto> login(UserLoginDto userLoginDto) {

        //validar aqui nulos de userLoginDto (username y email) ??

        return userMngConnector.getUserByUsernameOrEmail(userLoginDto.getUsername(), userLoginDto.getEmail())
                .filter(user -> user.getPassword() != null && passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword()))
                    .switchIfEmpty(Mono.error(new BadCredentialsException("Bad credentials, password doesn't match")))
                    .onErrorResume(BadCredentialsException.class, Mono::error)
                        .map(user -> {
                            UserDetailsImpl userDetails = new UserDetailsImpl(user.getUsername(), user.getPassword());
                            return new TokenDto(jwtProvider.generateToken(userDetails));
                        });
        }
}
