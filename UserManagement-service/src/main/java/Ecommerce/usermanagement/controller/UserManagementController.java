package Ecommerce.usermanagement.controller;

import Ecommerce.usermanagement.dto.cart.CartLinkUserDto;
import Ecommerce.usermanagement.dto.input.UserRegisterInternalDto;
import Ecommerce.usermanagement.dto.output.UserCreatedResponseDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.dto.output.UserLoginDto;
import Ecommerce.usermanagement.services.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/api/usermanagement")
@Validated
public class UserManagementController {

    private final UserManagementService userManagementService;

    @Autowired
    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }


    // GETS
    @GetMapping("/get/userBasic/{uuid}")
    public Mono<ResponseEntity<UserCreatedResponseDto>> getUserByUuidBasic(@PathVariable String uuid) {

        return userManagementService.getUserByUuid(uuid)
                .map(user -> ResponseEntity.ok(user))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/get/userInfo/{uuid}")
    public Mono<ResponseEntity<UserInfoOutputDto>> getUserInfoByUuid(@PathVariable String uuid) {

        return userManagementService.getUserInfoByUuid(uuid)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/get/userByEmail/{email}")
    public Mono<ResponseEntity<UserCreatedResponseDto>> getUserByEmail(@PathVariable String email) {

        return userManagementService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/get/userByUserName/{username}")
    public Mono<ResponseEntity<UserCreatedResponseDto>> getUserBasicByUsername(@PathVariable String username) {

        return userManagementService.getUserByUserName(username)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/get/allUsersInfo")
    public Mono<ResponseEntity<List<UserInfoOutputDto>>> getAllUsersInfoAsList() {

        return userManagementService.getAllUsersInfo()
                .map(users -> ResponseEntity.ok(users))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }


    //--->  CONNECTION WITH MYDATA-SERVICE
    @PutMapping("/internal/updateUserHasCart")
    public Mono<ResponseEntity<String>> updateUserHasCart(@RequestBody CartLinkUserDto cartLinkUserDto) {

        return userManagementService.linkCartToUser(cartLinkUserDto)
                .map(msg -> ResponseEntity.ok(msg));
    }


    //---> CONNECTION WITH AUTHENTICATION-SERVICE
        //---> Login
    @GetMapping("/internal/getUserLoginByUsername")
    public Mono<ResponseEntity<UserLoginDto>> getUserLoginByUsername(@RequestParam String username) {

        return userManagementService.getUserLoginByUserName(username)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/internal/getUserLoginByEmail")
    public Mono<ResponseEntity<UserLoginDto>> getUserLoginByEmail(@RequestParam String email) {

        return userManagementService.getUserLoginByEmail(email)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //---> Register
    @PostMapping("/internal/addUser")
    public Mono<ResponseEntity<UserCreatedResponseDto>> createAndSaveUser(@Valid @RequestBody UserRegisterInternalDto userInputDto) {

        return userManagementService.createUser(userInputDto)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user));
    }
}
