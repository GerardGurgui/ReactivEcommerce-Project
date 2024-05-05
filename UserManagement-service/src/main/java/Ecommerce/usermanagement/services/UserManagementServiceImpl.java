package Ecommerce.usermanagement.services;

import Ecommerce.usermanagement.document.Roles;
import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.cart.UserCartDto;
import Ecommerce.usermanagement.dto.input.UserEmailDto;
import Ecommerce.usermanagement.dto.input.UserInputDto;
import Ecommerce.usermanagement.dto.input.UserDto;
import Ecommerce.usermanagement.dto.output.UserBasicOutputDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.exceptions.EmailExistsException;
import Ecommerce.usermanagement.exceptions.EmailNotFoundException;
import Ecommerce.usermanagement.exceptions.UserNotFoundException;
import Ecommerce.usermanagement.exceptions.UsernameAlreadyExistsException;
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


    @Autowired
    private IUsersRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


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

    ////CHECKS

    private Mono<Boolean> checkIfUserExistsByEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    private Mono<Boolean> checkIfUserExistsByUsername(String username) {

        return userRepository.existsByUsername(username);
    }

    private String generateLastestAcces() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }


    ///// CRUD
    @Override
    public Mono<UserInfoOutputDto> addUser(UserInputDto userInputDto) {

    return Mono.when(
            checkIfUserExistsByEmail(userInputDto.getEmail())
                .doOnNext(emailExists -> {
                    if (emailExists) {
                        throw new EmailExistsException("Email already exists", userInputDto.getEmail());
                    }
                }),
            checkIfUserExistsByUsername(userInputDto.getUsername())
                .doOnNext(usernameExists -> {
                    if (usernameExists) {
                        throw new UsernameAlreadyExistsException("Username already exists", userInputDto.getUsername());
                    }
                })
        )
        .then(Mono.defer(() -> {
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
            return userRepository.save(user);
        }))
        .map(Converter::convertToDtoInfo);
    }

    ////GETS

    @Override
    public Mono<UserBasicOutputDto> getUserByUuid(String userUuidDto) {

    return userRepository.findByUuid(userUuidDto)
            .map(Converter::convertToDtoBasic)
            .switchIfEmpty(Mono.error(new UserNotFoundException("User not found", userUuidDto)));
    }

    @Override
    public Mono<UserInfoOutputDto> getUserInfoByUuid(String userUuidDto) {

        return userRepository.findByUuid(userUuidDto)
                .map(Converter::convertToDtoInfo)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found", userUuidDto)));

    }

    @Override
    public Mono<UserBasicOutputDto> getUserByEmail(UserEmailDto userEmailDto) {

        return userRepository.findByEmail(userEmailDto.getEmail())
                .map(Converter::convertToDtoBasic)
                .switchIfEmpty(Mono.error(new EmailNotFoundException("Email not found", userEmailDto.getEmail())));
    }


    public Flux<UserInfoOutputDto> getAllUsersInfo() {

        return userRepository.findAll()
                .map(Converter::convertToDtoInfo);
    }

    ////--> FLUX con operador .filter para filtrar por:
    // 1. user.isActive == true
    // 2. user.isActiveCart == true
    // 3. user.totalSpent > 0
    // 4. user.totalPurchase > 0
    // 5. user.getRoles().contains(Roles.USER)
    // 6. user.getRoles().contains(Roles.ADMIN) etc


    ////COMUNICACION CON MICROSERVICIO MYDATA

    ////CARTS
    public Mono<UserInfoOutputDto> updateUserHasCart(UserCartDto userCartDto) {

        //faltan comprobaciones y mejoras, de seguridad eso seguro

        return userRepository.findByUuid(userCartDto.getUserDto().getUuid())
                .flatMap(user -> {
                    user.setActiveCart(true);
                    user.addCart(userCartDto.getCartDto());
                    return userRepository.save(user);
                })
                .map(Converter::convertToDtoInfo)
        .switchIfEmpty(Mono.error(new UserNotFoundException("User not found", userCartDto.getUserDto().getUuid())));
    }

}
