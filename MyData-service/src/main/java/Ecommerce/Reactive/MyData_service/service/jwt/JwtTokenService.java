//package Ecommerce.Reactive.MyData_service.service.jwt;
//
//import Ecommerce.Reactive.MyData_service.DTO.UserDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//import java.util.Map;
//import java.util.logging.Logger;
//
//@Service
//public class JwtTokenService {
//
//    private static final Logger LOGGER =  Logger.getLogger(JwtTokenService.class.getName());
//
//    private final JwtToUserDtoConverter jwtToUserDtoConverter;
//
//    public JwtTokenService(JwtToUserDtoConverter jwtToUserDtoConverter) {
//        this.jwtToUserDtoConverter = jwtToUserDtoConverter;
//    }
//
//    public Mono<UserDto> extractUserFromToken() {
//
//        LOGGER.info("-----> Extracting user from token in JwtTokenService");
//
//        // Extract the JWT token from the security context and extract the user info from the "sub" claim
//
//        return ReactiveSecurityContextHolder.getContext()
//                // Extract Authentication from SecurityContext
//                .map(securityContext ->  securityContext.getAuthentication())
//                .cast(JwtAuthenticationToken.class)
//                // Extract Jwt token from Authentication
//                .map(JwtAuthenticationToken::getToken)
//                // Extract UserDto from Jwt token
//                .map(this::convertWithValidation)
//                .doOnSuccess(userDto -> LOGGER.info("User extracted successfully: " + userDto.getUsername()))
//                .onErrorResume((error) -> Mono.empty());
//    }
//
//    private UserDto convertWithValidation(Jwt jwt) {
//
//        UserDto user = jwtToUserDtoConverter.convert(jwt);
//
//        if (user.getUuid() == null || user.getUuid().isBlank()) {
//            throw new IllegalArgumentException("JWT claim 'uuid' is missing or empty");
//        }
//
//        if (user.getUsername() == null || user.getUsername().isBlank()) {
//            throw new IllegalArgumentException("JWT claim 'preferred_username' is missing or empty");
//        }
//
//        return user;
//    }
//
//
//}
//
