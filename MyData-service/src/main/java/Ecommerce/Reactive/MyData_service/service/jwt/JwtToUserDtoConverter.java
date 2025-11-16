package Ecommerce.Reactive.MyData_service.service.jwt;

import Ecommerce.Reactive.MyData_service.DTO.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtToUserDtoConverter implements Converter<Jwt, UserDto> {


    @Override
    public UserDto convert(Jwt jwt) {

        Map<String, Object> claims = jwt.getClaims();

        return UserDto.builder()
                .uuid((String) claims.getOrDefault("uuid", jwt.getSubject()))
                .username((String) claims.getOrDefault("preferred_username", jwt.getSubject()))
                .email((String) claims.getOrDefault("email", ""))
                .build();

        }
}
