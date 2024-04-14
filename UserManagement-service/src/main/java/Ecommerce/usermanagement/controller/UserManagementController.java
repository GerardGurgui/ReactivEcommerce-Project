package Ecommerce.usermanagement.controller;

import Ecommerce.usermanagement.dto.input.UserEmailDto;
import Ecommerce.usermanagement.dto.input.UserInputDto;
import Ecommerce.usermanagement.dto.input.UserUuidDto;
import Ecommerce.usermanagement.dto.output.UserBasicOutputDto;
import Ecommerce.usermanagement.services.UserManagementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/usermanagement")
public class UserManagementController {

    @Autowired
    private UserManagementServiceImpl userManagementService;

    @PostMapping("/addUser")
    public Mono<?> addUser(@RequestBody UserInputDto userInputDto) {

        return userManagementService.addUser(userInputDto);
    }

    @GetMapping("/getUserBasic/{uuid}")
    public Mono<UserBasicOutputDto> getUserByUuidBasic(@PathVariable String uuid) {

        return userManagementService.getUserByUuid(uuid);
    }

    @GetMapping("/getUserInfo/{uuid}")
    public Mono<?> getUserInfoByUuid(@PathVariable String uuid) {

        return userManagementService.getUserInfoByUuid(uuid);
    }

    @GetMapping("/getUserByEmail/{email}")
    public Mono<?> getUserInfoByEmail(@PathVariable UserEmailDto email) {

        return userManagementService.getUserByEmail(email);
    }


}
