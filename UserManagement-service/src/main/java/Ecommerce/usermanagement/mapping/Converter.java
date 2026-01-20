package Ecommerce.usermanagement.mapping;

import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.input.UserRegisterDto;
import Ecommerce.usermanagement.dto.output.UserProfileDto;
import Ecommerce.usermanagement.dto.output.UserOwnProfileDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.dto.output.UserLoginDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class Converter {

    public static User convertFromDtoToUser(UserRegisterDto userDto) {

        User user = new User();

        user.setUuid(userDto.getUuid());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPasswordHash());
        user.setName(userDto.getName());
        user.setLastname(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        //timestamps
        user.setRegisteredAt(userDto.getRegisteredAt());
        user.setLatestAccess(userDto.getRegisteredAt());
        //roles
        user.setRoles(List.of(userDto.getRole()));
        //audit info
        user.setRegistrationIp(userDto.getRegistrationIp());
        // account status
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        // purchase stats(initially zero for new user)
        user.setTotalPurchase(0L);
        user.setTotalSpent(BigDecimal.ZERO);

        return user;

    }

    public static UserProfileDto convertToUserProfileDto(User user) {

        UserProfileDto userDto = new UserProfileDto();

        BeanUtils.copyProperties(user, userDto);

        return userDto;
    }

    public static UserOwnProfileDto convertToOwnProfileDto(User user) {

        UserOwnProfileDto userDto = new UserOwnProfileDto();

        BeanUtils.copyProperties(user, userDto);

        //roles
        if(user.getRoles() != null){
            userDto.setRoles(new ArrayList<>(user.getRoles()));
        }

        return userDto;
    }

    public static UserInfoOutputDto convertToDtoInfo(User user) {

        UserInfoOutputDto userDto = new UserInfoOutputDto();

        BeanUtils.copyProperties(user, userDto);

        //roles
        if(user.getRoles() != null){
            userDto.setRoles(new ArrayList<>(user.getRoles()));
        }

        return userDto;
    }

    public static UserLoginDto convertToDtoLogin(User user) {

        UserLoginDto userDto = new UserLoginDto();

        BeanUtils.copyProperties(user, userDto);

        //roles
        if(user.getRoles() != null){
            userDto.setRoles(new ArrayList<>(user.getRoles()));
        }

        return userDto;
    }
}
