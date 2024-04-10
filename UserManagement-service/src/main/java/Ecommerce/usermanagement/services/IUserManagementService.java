package Ecommerce.usermanagement.services;

import Ecommerce.usermanagement.dto.input.UserEmailDto;
import Ecommerce.usermanagement.dto.input.UserInputDto;
import Ecommerce.usermanagement.dto.input.UserUuidDto;
import Ecommerce.usermanagement.dto.output.UserBasicOutputDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import reactor.core.publisher.Mono;

public interface IUserManagementService {

    Mono<?> addUser(UserInputDto userInputDto);

    Mono<UserBasicOutputDto> getUserByUuid(UserUuidDto userUuidDto);

    Mono<UserInfoOutputDto> getUserInfoByUuid(UserUuidDto userUuidDto);

    Mono<UserBasicOutputDto> getUserByEmail(UserEmailDto userEmailDto);
}
