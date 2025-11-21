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

import java.util.List;


@RestController
@RequestMapping(value = "/api/usermanagement")
@Validated
public class UserManagementController {


    //REVISAR MONO CON RESPONSE ENTITY AL REVEEES!

    private final UserManagementServiceImpl userManagementService;

    @Autowired
    public UserManagementController(UserManagementServiceImpl userManagementService) {
        this.userManagementService = userManagementService;
    }


    @PostMapping("/addUser")
    public ResponseEntity<Mono<UserInfoOutputDto>> addUser(@Valid @RequestBody UserInputDto userInputDto) {

        return new ResponseEntity<>(userManagementService.createAndSaveUser(userInputDto), HttpStatus.CREATED);
    }

    ////GETS

    @GetMapping("/get/userBasic/{uuid}")
    public Mono<ResponseEntity<UserBasicOutputDto>> getUserByUuidBasic(@PathVariable String uuid) {

        return userManagementService.getUserByUuid(uuid)
                .map(userBasicOutputDto -> new ResponseEntity<>(userBasicOutputDto, HttpStatus.FOUND));
    }

    @GetMapping("/get/userInfo/{uuid}")
    public ResponseEntity<Mono<UserInfoOutputDto>> getUserInfoByUuid(@PathVariable String uuid) {

        return new ResponseEntity<>(userManagementService.getUserInfoByUuid(uuid), HttpStatus.FOUND);
    }

    @GetMapping("/get/userByEmail/{email}")
    public ResponseEntity<Mono<UserBasicOutputDto>> getUserByEmail(@PathVariable String email) {

        return new ResponseEntity<>(userManagementService.getUserByEmail(email), HttpStatus.FOUND);
    }

    @GetMapping("/get/userByUserName/{username}")
    public Mono<ResponseEntity<UserBasicOutputDto>> getUserBasicByUsername(@PathVariable String username) {

        return userManagementService.getUserByUserName(username)
                .map(userBasicOutputDto -> new ResponseEntity<>(userBasicOutputDto, HttpStatus.FOUND));
    }


    @GetMapping("/get/allUsersInfo")
    public Mono<ResponseEntity<List<UserInfoOutputDto>>> getAllUsersInfoAsList() {

        return userManagementService.getAllUsersInfo()
                .map(userInfoOutputDtos -> new ResponseEntity<>(userInfoOutputDtos, HttpStatus.FOUND))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


        ////CONNECTION WITH MYDATA-SERVICE
    //CARTS

    @PutMapping("/updateUserHasCart/")
    public ResponseEntity<Mono<UserInfoOutputDto>> updateUserHasCart(@RequestBody CartLinkUserDto cartLinkUserDto){

        return new ResponseEntity<>(userManagementService.linkCartToUser(cartLinkUserDto), HttpStatus.OK);
    }

    ////CONNECTION WITH AUTHENTICATION-SERVICE
    //Login

    @GetMapping("/getUserLoginByUsername")
    public ResponseEntity<Mono<UserLoginDto>> getUserLoginByUsername(@RequestParam String username) {
        return new ResponseEntity<>(userManagementService.getUserLoginByUserName(username), HttpStatus.FOUND);
    }

    @GetMapping("/getUserLoginByEmail")
    public ResponseEntity<Mono<UserLoginDto>> getUserLoginByEmail(@RequestParam String email) {
        return new ResponseEntity<>(userManagementService.getUserLoginByEmail(email), HttpStatus.FOUND);
    }


}
