package Ecommerce.usermanagement.services;

import Ecommerce.usermanagement.document.Roles;
import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.cart.UserCartDto;
import Ecommerce.usermanagement.dto.input.UserEmailDto;
import Ecommerce.usermanagement.dto.input.UserInputDto;
import Ecommerce.usermanagement.dto.output.UserBasicOutputDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.dto.output.UserLoginDto;
import Ecommerce.usermanagement.exceptions.*;
import Ecommerce.usermanagement.mapping.Converter;
import Ecommerce.usermanagement.repository.IUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserManagementServiceImpl implements IUserManagementService {

    
    private final IUsersRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserManagementServiceImpl(IUsersRepository userRepository) {
        this.userRepository = userRepository;
    }


    private Set<Roles> assignRole(Set<String> rolenames){

        Set<Roles> userRoles = new HashSet<>();

        for (String rolename : rolenames) {

            if (rolename.equalsIgnoreCase("ADMIN")) {
                userRoles.add(Roles.ADMIN);

            } else if (rolename.equalsIgnoreCase("USER")) {
                userRoles.add(Roles.USER);
            }
        }
        return userRoles;
    }


    private String generateLastestAcces() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
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

    @Override
    public Mono<UserInfoOutputDto> addUser(UserInputDto userInputDto) {

        User user = Converter.convertFromDtoToUser(userInputDto);
        user.setUuid(UUID.randomUUID().toString());
        user.setLatestAccess(generateLastestAcces());
        user.setPassword(passwordEncoder.encode(userInputDto.getPassword()));
        user.setLoginDate(LocalDate.now());
        user.setTotalPurchase(0L);
        user.setTotalSpent(0);
        user.setActiveCart(false);
        user.setActive(true);
        user.addRoleUser(); //default role
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
    public Mono<UserBasicOutputDto> getUserByEmail(UserEmailDto userEmailDto) {

        return userRepository.findByEmail(userEmailDto.getEmail())
                .switchIfEmpty(Mono.error(new EmailNotFoundException("Email not found", userEmailDto.getEmail())))
                .onErrorResume(e -> Mono.error(new EmailNotFoundException("Error getting user by email:, Email not found", userEmailDto.getEmail())))
                .map(Converter::convertToDtoBasic);

    }

    //with password (Login)
    public Mono<UserLoginDto> getUserLoginByUserName(String userName) {

        return userRepository.findByUsername(userName)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with username: " + userName + " not found")))
                .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting user by username: " + userName + ", User not found")))
                .map(Converter::convertToDtoLogin);
    }

    //with password (Login)
    public Mono<UserLoginDto> getUserLoginByEmail(UserEmailDto userEmailDto) {

        return userRepository.findByEmail(userEmailDto.getEmail())
                .switchIfEmpty(Mono.error(new EmailNotFoundException("Email not found", userEmailDto.getEmail())))
                .onErrorResume(e -> Mono.error(new EmailNotFoundException("Error getting user by email:, Email not found", userEmailDto.getEmail())))
                .map(Converter::convertToDtoLogin);

    }



    public Flux<UserInfoOutputDto> getAllUsersInfo() {

        return userRepository.findAll()
                .switchIfEmpty(Mono.error(new UserNotFoundException("No users found")))
                .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting users, No users found")))
                .map(Converter::convertToDtoInfo);

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

    public Mono<UserLoginDto> getUserByUsernameOrEmail(String username, String email) {

        if (username == null || email == null) {
            return Mono.error(new UserNotFoundException("Username or email is null"));
        }

        if (username.isEmpty() && email.isEmpty()) {
            return Mono.error(new UserNotFoundException("Username and email are empty"));
        }

        if (username.isEmpty()) {
            return getUserLoginByEmail(new UserEmailDto(email));
        }

        if (email.isEmpty()) {
            return getUserLoginByUserName(username);
        }

        return getUserByUserNameAndEmail(username, email);
    }

    private Mono<UserLoginDto> getUserByUserNameAndEmail(String username, String email) {

        return userRepository.findByUsernameAndEmail(username, email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with username: " + username + " and email: " + email + " not found")))
                .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting user by username and email:, User not found")))
                .map(Converter::convertToDtoLogin);
    }


    /////-----> COMUNICACION CON MICROSERVICIO MYDATA

    //FALTA MODIFICAR EL ESTADO DE LOS CARRITOS DE USUARIO
    //FALTA CAMBIAR ORDEN DE LA CADENA, PRIMERO COMPROBAR ERRORES, SI TODO OK , ENTONCES ADELANTE

    ////CARTS
    public Mono<UserInfoOutputDto> updateUserHasCart(UserCartDto userCartDto) {

        //faltan comprobaciones y mejoras, de seguridad eso seguro

        String userDtoUuid = userCartDto.getUserDto().getUuid();

        return userRepository.findByUuid(userDtoUuid)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with Uuid: " + userDtoUuid + " not found")))
                .flatMap(user -> {
                    user.setActiveCart(true);
                    user.addCart(userCartDto.getCartDto());
                    return userRepository.save(user)
                            .map(Converter::convertToDtoInfo);
                });


    }

}
