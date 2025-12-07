package Ecommerce.usermanagement.mapping;

import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.input.UserRegisterInternalDto;
import Ecommerce.usermanagement.dto.output.UserCreatedResponseDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.dto.output.UserLoginDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Converter {

    public static User convertFromDtoToUser(UserRegisterInternalDto userDto) {

        User user = new User();

        user.setUuid(userDto.getUuid());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPasswordHash());
        user.setName(userDto.getName());
        user.setLastname(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        //timestamps
        user.setLoginDate(userDto.getRegisteredAt());
        user.setLatestAccess(userDto.getRegisteredAt());
        //roles
        user.setRoles(List.of(userDto.getRole()));
        //audit info
        user.setRegistrationIp(userDto.getRegistrationIp());
        //other fields can be set to default or null
        user.setCartIds(new ArrayList<>());
        user.setOrderIds(new ArrayList<>());
        // account status
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);

        return user;

    }

    public static UserCreatedResponseDto convertToDtoBasic(User user) {

        UserCreatedResponseDto userDto = new UserCreatedResponseDto();

        BeanUtils.copyProperties(user, userDto);

        return userDto;
    }

    public static UserInfoOutputDto convertToDtoInfo(User user) {

        UserInfoOutputDto userDto = new UserInfoOutputDto();

        BeanUtils.copyProperties(user, userDto);

        //roles
        if(user.getRoles() != null){
            userDto.setRoles(new ArrayList<>(user.getRoles()));
        }

        //carritos

        return userDto;
    }

    public static UserLoginDto convertToDtoLogin(User user) {

        UserLoginDto userDto = new UserLoginDto();

        BeanUtils.copyProperties(user, userDto);

        //roles
        if(user.getRoles() != null){
            userDto.setRoles(new ArrayList<>(user.getRoles()));
        }

        //carritos

        return userDto;
    }
}
