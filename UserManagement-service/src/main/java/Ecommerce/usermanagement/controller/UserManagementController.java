package Ecommerce.usermanagement.controller;

import Ecommerce.usermanagement.dto.cart.CartLinkUserDto;
import Ecommerce.usermanagement.dto.input.UserInputDto;
import Ecommerce.usermanagement.dto.output.UserBasicOutputDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.dto.output.UserLoginDto;
import Ecommerce.usermanagement.services.UserManagementServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;


@RestController
@RequestMapping("/api/usermanagement")
@Validated
public class UserManagementController {

    private final UserManagementServiceImpl userManagementService;

    @Autowired
    public UserManagementController(UserManagementServiceImpl userManagementService) {
        this.userManagementService = userManagementService;
    }

    // Create user
    @PostMapping("/addUser")
    public Mono<ResponseEntity<UserInfoOutputDto>> addUser(@Valid @RequestBody UserInputDto userInputDto) {

        return userManagementService.createAndSaveUser(userInputDto)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user));
    }


    // GETS
    @GetMapping("/get/userBasic/{uuid}")
    public Mono<ResponseEntity<UserBasicOutputDto>> getUserByUuidBasic(@PathVariable String uuid) {

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
    public Mono<ResponseEntity<UserBasicOutputDto>> getUserByEmail(@PathVariable String email) {

        return userManagementService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/get/userByUserName/{username}")
    public Mono<ResponseEntity<UserBasicOutputDto>> getUserBasicByUsername(@PathVariable String username) {

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


    // CONNECTION WITH MYDATA-SERVICE
    @PutMapping("/updateUserHasCart")
    public Mono<ResponseEntity<String>> updateUserHasCart(@RequestBody CartLinkUserDto cartLinkUserDto) {

        return userManagementService.linkCartToUser(cartLinkUserDto)
                .map(msg -> ResponseEntity.ok(msg));
    }


    //---> CONNECTION WITH AUTHENTICATION-SERVICE
    @GetMapping("/getUserLoginByUsername")
    public Mono<ResponseEntity<UserLoginDto>> getUserLoginByUsername(@RequestParam String username) {

        return userManagementService.getUserLoginByUserName(username)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/getUserLoginByEmail")
    public Mono<ResponseEntity<UserLoginDto>> getUserLoginByEmail(@RequestParam String email) {

        return userManagementService.getUserLoginByEmail(email)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
