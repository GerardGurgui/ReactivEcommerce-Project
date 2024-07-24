package Ecommerce.usermanagement.mapping;

import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.cart.CartDto;
import Ecommerce.usermanagement.dto.input.UserInputDto;
import Ecommerce.usermanagement.dto.output.UserBasicOutputDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.dto.output.UserLoginDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class Converter {

    public static User convertFromDtoToUser(UserInputDto userDto) {

        User user = new User();

        BeanUtils.copyProperties(userDto, user);
        return user;

    }

    public static UserBasicOutputDto convertToDtoBasic(User user) {

        UserBasicOutputDto userDto = new UserBasicOutputDto();

        BeanUtils.copyProperties(user, userDto);

        return userDto;
    }

    public static UserInfoOutputDto convertToDtoInfo(User user) {

        UserInfoOutputDto userDto = new UserInfoOutputDto();

        BeanUtils.copyProperties(user, userDto);

        //roles
        if(user.getRoles() != null){
            userDto.setRoles(new HashSet<>(user.getRoles()));
        }

        //carritos



        return userDto;
    }

    public static UserLoginDto convertToDtoLogin(User user) {

        UserLoginDto userDto = new UserLoginDto();

        BeanUtils.copyProperties(user, userDto);

        //roles
        if(user.getRoles() != null){
            userDto.setRoles(new HashSet<>(user.getRoles()));
        }

        //carritos

        return userDto;
    }
}
