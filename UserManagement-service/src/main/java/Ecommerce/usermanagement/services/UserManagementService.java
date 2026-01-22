package Ecommerce.usermanagement.services;

import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.input.UserRegisterDto;
import Ecommerce.usermanagement.dto.output.UserProfileDto;
import Ecommerce.usermanagement.dto.output.UserOwnProfileDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.dto.output.UserLoginDto;
import Ecommerce.usermanagement.exceptions.*;
import Ecommerce.usermanagement.mapping.Converter;
import Ecommerce.usermanagement.repository.IUsersRepository;
import Ecommerce.usermanagement.security.config.SecurityUtils;
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
    private final SecurityUtils securityUtils;

    public UserManagementService(IUsersRepository userRepository,
                                 ReactiveMongoTemplate reactiveMongoTemplate,
                                 SecurityUtils securityUtils) {
        this.userRepository = userRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.securityUtils = securityUtils;
    }


    //// --->  CRUD
    ///  * Security note: Rate limiting is enforced at the API Gateway level
    ///  * (5 requests/second per IP) to prevent user enumeration attacks.
    public Mono<UserOwnProfileDto> createUser(UserRegisterDto userInputDto) {

        return checkUsername(userInputDto.getUsername())
                .then(Mono.defer(() -> checkEmail(userInputDto.getEmail())))
                .then(Mono.just(userInputDto))
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


    /// //FALTA UPDATE PARA QUE EL USUARIO PUEDA MODIFICAR SUS DATOS

    private UserOwnProfileDto toUserCreatedDto(User user) {

        return UserOwnProfileDto.builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .registeredAt(user.getRegisteredAt())
                .roles(user.getRoles())
                .totalSpent(user.getTotalSpent())
                .totalPurchase(user.getTotalPurchase())
                .build();
    }

    /// /GETS
    public Mono<UserOwnProfileDto> getMyProfileFromJwt() {

        return securityUtils.extractUserUuidFromJwt()
                .flatMap(this::getMyProfile);
    }

    public Mono<UserOwnProfileDto> getMyProfile(String userUuidDto) {

        return userRepository.findByUuid(userUuidDto)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with Uuid: " + userUuidDto + " not found")))
                .map(Converter::convertToOwnProfileDto);
    }


    public Mono<UserProfileDto> getUserProfile(String userUuidDto) {

        return userRepository.findByUuid(userUuidDto)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with Uuid: " + userUuidDto + " not found")))
                .map(Converter::convertToUserProfileDto);
    }

    //without password
    public Mono<UserProfileDto> getUserByUserName(String userName) {

        return userRepository.findByUsername(userName)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with username: " + userName + " not found")))
                .map(Converter::convertToUserProfileDto);
    }

    //without password
    public Mono<UserProfileDto> getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new EmailNotFoundException("Email not found", email)))
                .map(Converter::convertToUserProfileDto);
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


    /// // ----> COMUNICACION CON MICROSERVICIO USERAUTHENTICATION - LOGIN

    //with password (Login)
    public Mono<UserLoginDto> getUserLoginByUserName(String username) {

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with username: " + username + " not found")))
                .map(Converter::convertToDtoLogin);
    }

    //with password (Login)
    public Mono<UserLoginDto> getUserLoginByEmail(String email) {

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new EmailNotFoundException("Email not found", email)))
                .map(Converter::convertToDtoLogin);

    }

    //Update the latestAccess field
    /**
     * Updates the latest access timestamp for a user based on a login event.
     * This method is typically called when a user successfully logs in, by kafka listener.
     */
    public Mono<Void> updateLatestAccess(String userUuid, Instant loginTime) {

        Query query = Query.query(Criteria.where("uuid").is(userUuid));
        Update update = new Update().set("latest_access", loginTime);

        return reactiveMongoTemplate.updateFirst(query, update, User.class)
                .doOnNext(result -> {
                    if (result.getModifiedCount() == 0){ // if no modification was made log a warning
                        LOGGER.warning("Failed to updated latestAccess for user: " + userUuid);
                    } else {
                        LOGGER.info("Successfully updated latestAccess for user " + userUuid);
                    }
                }).then();
    }
}

