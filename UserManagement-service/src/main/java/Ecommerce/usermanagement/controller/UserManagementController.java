package Ecommerce.usermanagement.controller;

import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.input.UserEmailDto;
import Ecommerce.usermanagement.dto.input.UserInputDto;
import Ecommerce.usermanagement.dto.input.UserUuidDto;
import Ecommerce.usermanagement.dto.output.UserBasicOutputDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.services.UserManagementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/usermanagement")
public class UserManagementController {


    @Autowired
    private UserManagementServiceImpl userManagementService;


    @PostMapping("/addUser")
    public ResponseEntity<Mono<UserInfoOutputDto>> addUser(@RequestBody UserInputDto userInputDto) {

        return new ResponseEntity<>(userManagementService.addUser(userInputDto), HttpStatus.CREATED);
    }

    ////GETS

    @GetMapping("/getUserBasic/{uuid}")
    public ResponseEntity<Mono<UserBasicOutputDto>> getUserByUuidBasic(@PathVariable String uuid) {

        return new ResponseEntity<>(userManagementService.getUserByUuid(uuid), HttpStatus.FOUND);
    }

    @GetMapping("/getUserInfo/{uuid}")
    public ResponseEntity<Mono<UserInfoOutputDto>> getUserInfoByUuid(@PathVariable String uuid) {

        return new ResponseEntity<>(userManagementService.getUserInfoByUuid(uuid), HttpStatus.FOUND);
    }

    @GetMapping("/getUserByEmail/{email}")
    public ResponseEntity<Mono<UserBasicOutputDto>> getUserInfoByEmail(@PathVariable UserEmailDto email) {

        return new ResponseEntity<>(userManagementService.getUserByEmail(email), HttpStatus.FOUND);
    }

    @GetMapping("/getAllUsersInfo")
    public ResponseEntity<Flux<UserInfoOutputDto>> getAllUsersInfo() {

        return new ResponseEntity<>(userManagementService.getAllUsersInfo(), HttpStatus.FOUND);
    }

    ////CONNECTION WITH MYDATA-SERVICE
    //CARTS

    //actualiza el campo hasCart del usuario en caso de que añada un nuevo carrito a su lista
    @PutMapping("/updateUserHasCart/{uuid}")
    public ResponseEntity<Mono<UserInfoOutputDto>> updateUserHasCart(@PathVariable String uuid){

        return new ResponseEntity<>(userManagementService.updateUserHasCart(uuid), HttpStatus.OK);
    }


}
