package Ecommerce.usermanagement.services;

import Ecommerce.usermanagement.document.Roles;
import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.cart.CartLinkUserDto;
import Ecommerce.usermanagement.dto.input.UserInputDto;
import Ecommerce.usermanagement.dto.output.UserBasicOutputDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.dto.output.UserLoginDto;
import Ecommerce.usermanagement.exceptions.*;
import Ecommerce.usermanagement.mapping.Converter;
import Ecommerce.usermanagement.repository.IUsersRepository;
import com.thoughtworks.xstream.core.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserManagementServiceImpl implements IUserManagementService {

    private final IUsersRepository userRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserManagementServiceImpl(IUsersRepository userRepository,
                                     ReactiveMongoTemplate reactiveMongoTemplate) {
        this.userRepository = userRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }


    ////CHECKS
    @Override
    public Mono<Void> checkEmailExists(String email) {

        return userRepository.existsByEmail(email)
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new EmailExistsException("Email already exists", email));
                    } else {
                        return Mono.empty();
                    }
                });
    }

    @Override
    public Mono<Void> checkUsernameExists(String username) {

        return userRepository.existsByUsername(username)
                .flatMap(usernameExists -> {
                    if (usernameExists) {
                        return Mono.error(new UsernameAlreadyExistsException("Username already exists", username));
                    } else {
                        return Mono.empty();
                    }
                });
    }

    @Override
    public Mono<UserInfoOutputDto> createAndSaveUser(UserInputDto userInputDto) {

        Mono<Void> checkEmail = checkEmailExists(userInputDto.getEmail())
                .onErrorResume(e -> Mono.error(new EmailExistsException("Email already exists", userInputDto.getEmail())));

        Mono<Void> checkUsername = checkUsernameExists(userInputDto.getUsername())
                .onErrorResume(e -> Mono.error(new UsernameAlreadyExistsException("Username already exists", userInputDto.getUsername())));

        return Mono.when(checkEmail, checkUsername)
                .then(addUser(userInputDto));
    }



    ///// CRUD

    /////FALTA UPDATE PARA QUE EL USUARIO PUEDA MODIFICAR SUS DATOS

    @Override
    public Mono<UserInfoOutputDto> addUser(UserInputDto userInputDto) {

        User user = Converter.convertFromDtoToUser(userInputDto);
        user.setUuid(UUID.randomUUID().toString());
        user.setLatestAccess(generateLatestAccess());
        user.setPassword(passwordEncoder.encode(userInputDto.getPassword()));
        user.setLoginDate(LocalDate.now());
        user.setTotalPurchase(0L);
        user.setTotalSpent(0);
        user.addRoleUser(); //default role
        user.getAuthorities();
        //userDetails properties
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setEnabled(true);
        return userRepository.save(user).map(Converter::convertToDtoInfo);
    }

    ////GET

    @Override
    public Mono<UserBasicOutputDto> getUserByUuid(String userUuidDto) {

    return userRepository.findByUuid(userUuidDto)
            .switchIfEmpty(Mono.error(new UserNotFoundException("User with Uuid: " + userUuidDto + " not found")))
            .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting user by Uuid: " + userUuidDto + ", User not found")))
            .map(Converter::convertToDtoBasic);
    }

    @Override
    public Mono<UserInfoOutputDto> getUserInfoByUuid(String userUuidDto) {

        return userRepository.findByUuid(userUuidDto)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with Uuid: " + userUuidDto + " not found")))
                .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting user by Uuid: " + userUuidDto + ", User not found")))
                .map(Converter::convertToDtoInfo);
    }

    //without password
    @Override
    public Mono<UserBasicOutputDto> getUserByUserName(String userName) {

        return userRepository.findByUsername(userName)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with username: " + userName + " not found")))
                .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting user by username: " + userName + ", User not found")))
                .map(Converter::convertToDtoBasic);
    }

    //without password
    @Override
    public Mono<UserBasicOutputDto> getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new EmailNotFoundException("Email not found",email)))
                .onErrorResume(e -> Mono.error(new EmailNotFoundException("Error getting user by email:, Email not found", email)))
                .map(Converter::convertToDtoBasic);
    }


    public Mono<List<UserInfoOutputDto>> getAllUsersInfo() {
        return userRepository.findAll()
                .map(Converter::convertToDtoInfo)      // convierte cada User a DTO
                .collectList()                         // agrupa en List<UserInfoOutputDto>
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Mono.error(new UserNotFoundException("No users found"));
                    }
                    return Mono.just(list);
                })
                .onErrorMap(e -> {
                    if (e instanceof UserNotFoundException) return e;
                    return new UserNotFoundException("Error getting users");
                });
    }

    ////--> FLUX con operador .filter para filtrar por:
    // 1. user.isActive == true
    // 2. user.isActiveCart == true
    // 3. user.totalSpent > 0
    // 4. user.totalPurchase > 0
    // 5. user.getRoles().contains(Roles.USER)
    // 6. user.getRoles().contains(Roles.ADMIN) etc


    ///// ----> COMUNICACION CON MICROSERVICIO USERAUTHENTICATION - LOGIN

    //QUE IMPLICA EL LOGIN?
    //Moodificar LastestAccess
    // que mas ?

    //with password (Login)
    public Mono<UserLoginDto> getUserLoginByUserName(String username) {

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with username: " + username + " not found")))
                .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting user by username: " + username + ", User not found")))
                .map(Converter::convertToDtoLogin);
    }

    //with password (Login)
    public Mono<UserLoginDto> getUserLoginByEmail(String email) {

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new EmailNotFoundException("Email not found", email)))
                .onErrorResume(e -> Mono.error(new EmailNotFoundException("Error getting user by email:, Email not found", email)))
                .map(Converter::convertToDtoLogin);

    }

    //Update the latestAccess field
    public Mono<Void> updateLatestAccess(String userUuid, Instant loginTime) {

        Query query = Query.query(Criteria.where("uuid").is(userUuid));
        Update update = new Update().set("latestAccess", loginTime);

        return reactiveMongoTemplate.updateFirst(query, update, User.class)
                .then();
    }


    /////-----> MYDATA - CARTS

    ////CARTS
    public Mono<String> linkCartToUser(CartLinkUserDto cartLinkUserDto) {

        String userUuid = cartLinkUserDto.getUserUuid();

        return userRepository.findByUuid(userUuid)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with Uuid: " + userUuid + " not found")))
                .flatMap(user -> {
                    user.addCartId(cartLinkUserDto.getIdCart());
                    return userRepository.save(user)
                            .map(u -> "Cart linked successfully to user with UUID: " + userUuid);
                });

    }


    ////UTILITY METHODS
    private Set<Roles> assignRole(Set<String> rolenames){

        Set<Roles> userRoles = new HashSet<>();

        for (String rolename : rolenames) {

            if (rolename.equalsIgnoreCase("ADMIN")) {
                userRoles.add(Roles.ROLE_ADMIN);

            } else if (rolename.equalsIgnoreCase("USER")) {
                userRoles.add(Roles.ROLE_USER);
            }
        }
        return userRoles;
    }


    private String generateLatestAccess() {

        return ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
