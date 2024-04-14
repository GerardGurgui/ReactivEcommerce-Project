package Ecommerce.usermanagement.services;

import Ecommerce.usermanagement.dto.input.UserEmailDto;
import Ecommerce.usermanagement.dto.input.UserInputDto;
import Ecommerce.usermanagement.dto.input.UserUuidDto;
import Ecommerce.usermanagement.dto.output.UserBasicOutputDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import reactor.core.publisher.Mono;

public interface IUserManagementService {

    Mono<?> addUser(UserInputDto userInputDto);

    Mono<UserBasicOutputDto> getUserByUuid(String userUuidDto);

    Mono<UserInfoOutputDto> getUserInfoByUuid(String userUuidDto);

    Mono<UserBasicOutputDto> getUserByEmail(UserEmailDto userEmailDto);
}
