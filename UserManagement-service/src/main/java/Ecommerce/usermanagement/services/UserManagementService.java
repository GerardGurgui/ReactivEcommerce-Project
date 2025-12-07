package Ecommerce.usermanagement.services;

import Ecommerce.usermanagement.document.Roles;
import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.cart.CartLinkUserDto;
import Ecommerce.usermanagement.dto.input.UserRegisterInternalDto;
import Ecommerce.usermanagement.dto.output.UserCreatedResponseDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.dto.output.UserLoginDto;
import Ecommerce.usermanagement.exceptions.*;
import Ecommerce.usermanagement.mapping.Converter;
import Ecommerce.usermanagement.repository.IUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

@Service
public class UserManagementService {

    private final Logger LOGGER = Logger.getLogger(UserManagementService.class.getName());

    private final IUsersRepository userRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserManagementService(IUsersRepository userRepository,
                                 ReactiveMongoTemplate reactiveMongoTemplate) {
        this.userRepository = userRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    ///// CRUD
    public Mono<UserCreatedResponseDto> createUser(UserRegisterInternalDto dto) {

        return checkUsername(dto.getUsername())
                .then(checkEmail(dto.getEmail()))
                .then(Mono.just(dto))
                .map(Converter::convertFromDtoToUser)
                .flatMap(userRepository::save)
                .map(this::toUserCreatedDto);
    }

    private Mono<Void> checkUsername(String username) {

        return userRepository.existsByUsername(username)
                .flatMap(exists -> {
                    if (exists) {
                        LOGGER.info("Username already exists: " + username);
                        return Mono.error(new UsernameAlreadyExistsException("Username already exists: " + username));
                    } else {
                        return Mono.empty();
                    }
                });
    }

    private Mono<Void> checkEmail(String email) {

        return userRepository.existsByEmail(email)
                .flatMap(exists -> {
                    if (exists) {
                        LOGGER.info("Email already exists: " + email);
                        return Mono.error(new EmailAlreadyExistsException("Email already exists: " + email));
                    } else {
                        return Mono.empty();
                    }
                });
    }


    /////FALTA UPDATE PARA QUE EL USUARIO PUEDA MODIFICAR SUS DATOS

    private UserCreatedResponseDto toUserCreatedDto(User user) {

        return UserCreatedResponseDto.builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getLoginDate())
                .build();
    }

    ////GET
    public Mono<UserCreatedResponseDto> getUserByUuid(String userUuidDto) {

    return userRepository.findByUuid(userUuidDto)
            .switchIfEmpty(Mono.error(new UserNotFoundException("User with Uuid: " + userUuidDto + " not found")))
            .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting user by Uuid: " + userUuidDto + ", User not found")))
            .map(Converter::convertToDtoBasic);
    }


    public Mono<UserInfoOutputDto> getUserInfoByUuid(String userUuidDto) {

        return userRepository.findByUuid(userUuidDto)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with Uuid: " + userUuidDto + " not found")))
                .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting user by Uuid: " + userUuidDto + ", User not found")))
                .map(Converter::convertToDtoInfo);
    }

    //without password
    public Mono<UserCreatedResponseDto> getUserByUserName(String userName) {

        return userRepository.findByUsername(userName)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with username: " + userName + " not found")))
                .onErrorResume(e -> Mono.error(new UserNotFoundException("Error getting user by username: " + userName + ", User not found")))
                .map(Converter::convertToDtoBasic);
    }

    //without password
    public Mono<UserCreatedResponseDto> getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new EmailNotFoundException("Email not found",email)))
                .onErrorResume(e -> Mono.error(new EmailNotFoundException("Error getting user by email:, Email not found", email)))
                .map(Converter::convertToDtoBasic);
    }


    public Mono<List<UserInfoOutputDto>> getAllUsersInfo() {
        return userRepository.findAll()
                .map(Converter::convertToDtoInfo)
                .collectList()
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
    /**
     * Updates the latest access timestamp for a user based on a login event.
     * This method is typically called when a user successfully logs in.
     *
     * @param userUuid the UUID of the user whose latest access is being updated
     * @param loginTime the timestamp of the login event
     * @return a Mono that completes when the update is successful
     * @throws org.springframework.dao.DataAccessException if the update fails due to a database error
     */
    public Mono<Void> updateLatestAccess(String userUuid, Instant loginTime) {

        Query query = Query.query(Criteria.where("uuid").is(userUuid));
        Update update = new Update().set("latest_access", loginTime);

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

}
