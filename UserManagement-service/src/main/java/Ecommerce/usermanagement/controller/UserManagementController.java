package Ecommerce.usermanagement.controller;

import Ecommerce.usermanagement.dto.input.UserRegisterDto;
import Ecommerce.usermanagement.dto.output.UserProfileDto;
import Ecommerce.usermanagement.dto.output.UserOwnProfileDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.dto.output.UserLoginDto;
import Ecommerce.usermanagement.services.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@Validated
@RequestMapping("/api/usermanagement")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @Autowired
    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }


    // GETS
    //--> Basic User Info - profile
    @GetMapping("/users/me")
    @PreAuthorize("isAuthenticated()") // Get own profile from JWT token, only if authenticated
    public Mono<ResponseEntity<UserOwnProfileDto>> getMyProfile() {

        return userManagementService.getMyProfileFromJwt()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/users/uuid/{uuid}")
    public Mono<ResponseEntity<UserProfileDto>> getUserProfile(@PathVariable String uuid) {

        return userManagementService.getUserProfile(uuid)
                .map(ResponseEntity::ok);
    }

    // INTERNAL USE ONLY OR ADMIN ROLE
    @GetMapping("/users/email/{email}")
    public Mono<ResponseEntity<UserProfileDto>> getUserByEmail(@PathVariable String email) {

        return userManagementService.getUserByEmail(email)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/users/username/{username}")
    public Mono<ResponseEntity<UserProfileDto>> getUserByUsername(@PathVariable String username) {

        return userManagementService.getUserByUserName(username)
                .map(ResponseEntity::ok);
    }

    //FALTA PAGINACION Y FILTROS, SOLO ADMINS
    @GetMapping("/users/list")
    public Mono<ResponseEntity<List<UserInfoOutputDto>>> getAllUsersInfoAsList() {

        return userManagementService.getAllUsersInfo()
                .map(users -> ResponseEntity.ok(users));
    }


    //---> CONNECTION WITH AUTHENTICATION-SERVICE
        //---> Login
    @GetMapping("/internal/getUserLoginByUsername")
    public Mono<ResponseEntity<UserLoginDto>> getUserLoginByUsername(@RequestParam String username) {

        return userManagementService.getUserLoginByUserName(username)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/internal/getUserLoginByEmail")
    public Mono<ResponseEntity<UserLoginDto>> getUserLoginByEmail(@RequestParam String email) {

        return userManagementService.getUserLoginByEmail(email)
                .map(ResponseEntity::ok);
    }

    //---> Register
    @PostMapping("/internal/addUser")
    public Mono<ResponseEntity<UserOwnProfileDto>> createAndSaveUser(@Valid @RequestBody UserRegisterDto userInputDto) {

        return userManagementService.createUser(userInputDto)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user));
    }
}
