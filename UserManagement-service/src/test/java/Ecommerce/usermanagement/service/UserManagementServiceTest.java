package Ecommerce.usermanagement.service;

import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.input.UserRegisterDto;
import Ecommerce.usermanagement.dto.output.UserOwnProfileDto;
import Ecommerce.usermanagement.exceptions.EmailAlreadyExistsException;
import Ecommerce.usermanagement.exceptions.EmailNotFoundException;
import Ecommerce.usermanagement.exceptions.UserNotFoundException;
import Ecommerce.usermanagement.exceptions.UsernameAlreadyExistsException;
import Ecommerce.usermanagement.repository.IUsersRepository;
import Ecommerce.usermanagement.security.config.SecurityUtils;
import Ecommerce.usermanagement.services.UserManagementService;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {


    @Mock
    private IUsersRepository userRepository;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private UserManagementService userMngservice;

    private UserRegisterDto userDtoTest;
    //to check created user same as user register dto
    private User userTest;
    //to check get my profile
    private UserOwnProfileDto userOwnProfileDtoTest;
    private List<String> roles;
    private Instant testLoginTime;

    @BeforeEach
    public void setup() {

        userDtoTest = new UserRegisterDto();
        userDtoTest.setUuid("abcd1234");
        userDtoTest.setUsername("testuser");
        userDtoTest.setEmail("testuser@test.com");
        userDtoTest.setRole("USER");

        userTest = new User();
        userTest.setUuid(userDtoTest.getUuid());
        userTest.setUsername(userDtoTest.getUsername());
        userTest.setEmail(userDtoTest.getEmail());
        userTest.setRegisteredAt(Instant.now());

        userOwnProfileDtoTest = new UserOwnProfileDto();
        userOwnProfileDtoTest.setUuid(userTest.getUuid());
        userOwnProfileDtoTest.setUsername(userTest.getUsername());
        userOwnProfileDtoTest.setEmail(userTest.getEmail());
        userOwnProfileDtoTest.setRegisteredAt(userTest.getRegisteredAt());
        userOwnProfileDtoTest.setRoles(roles);

        roles = new ArrayList<>();
        roles.add(userDtoTest.getRole());
        userTest.setRoles(roles);

        testLoginTime = Instant.now();

    }

    // --> CREATE USER TESTS

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        void testCreateUser_Success() {

            when(userRepository.existsByUsername(userDtoTest.getUsername())).thenReturn(Mono.just(false));
            when(userRepository.existsByEmail(userDtoTest.getEmail())).thenReturn(Mono.just(false));
            when(userRepository.save(any(User.class))).thenReturn(Mono.just(userTest));

            StepVerifier.create(userMngservice.createUser(userDtoTest))
                    .assertNext(response -> {
                        assertEquals("abcd1234", response.getUuid());
                        assertEquals("testuser", response.getUsername());
                        assertEquals("testuser@test.com", response.getEmail());
                        assertNotNull(response.getRegisteredAt());
                        assertNotNull(response.getRoles());
                    })
                    .verifyComplete();

            verify(userRepository, times(1)).existsByUsername("testuser");
            verify(userRepository, times(1)).existsByEmail("testuser@test.com");
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void testCreateUser_UsernameAlreadyExists(){

            when(userRepository.existsByUsername(userDtoTest.getUsername())).thenReturn(Mono.just(true));

            StepVerifier.create(userMngservice.createUser(userDtoTest))
                    .expectError(UsernameAlreadyExistsException.class)
                    .verify();

            verify(userRepository, times(1)).existsByUsername("testuser");
            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));

        }

        @Test
        void testCreateUser_EmailAlreadyExists(){

            when(userRepository.existsByUsername(userDtoTest.getUsername())).thenReturn(Mono.just(false));
            when(userRepository.existsByEmail(userDtoTest.getEmail())).thenReturn(Mono.just(true));

            StepVerifier.create(userMngservice.createUser(userDtoTest))
                    .expectError(EmailAlreadyExistsException.class)
                    .verify();

            verify(userRepository, times(1)).existsByUsername("testuser");
            verify(userRepository, times(1)).existsByEmail("testuser@test.com");
            verify(userRepository, never()).save(any(User.class));

        }

        @Test
        void testCreateUser_DatabaseError() {

            when(userRepository.existsByUsername(userDtoTest.getUsername()))
                    .thenReturn(Mono.just(false));
            when(userRepository.existsByEmail(userDtoTest.getEmail()))
                    .thenReturn(Mono.just(false));
            when(userRepository.save(any(User.class)))
                    .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

            StepVerifier.create(userMngservice.createUser(userDtoTest))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(userRepository, times(1)).existsByUsername("testuser");
            verify(userRepository, times(1)).existsByEmail("testuser@test.com");
            verify(userRepository, times(1)).save(any(User.class));
        }

    }


    // --> GETS

    @Nested
    @DisplayName("Get My Profile From JWT Tests")
    class GetMyProfileFromJwtTests {

        @Test
        @DisplayName("Success - Get own profile from JWT")
        void getMyProfileFromJwt_Success() {
            when(securityUtils.extractUserUuidFromJwt()).thenReturn(Mono.just("abcd1234"));
            when(userRepository.findByUuid("abcd1234")).thenReturn(Mono.just(userTest));

            StepVerifier.create(userMngservice.getMyProfileFromJwt())
                    .assertNext(response -> {
                        assertEquals("abcd1234", response.getUuid());
                        assertEquals("testuser", response.getUsername());
                        assertEquals("testuser@test.com", response.getEmail());
                        assertNotNull(response.getRegisteredAt());
                        assertNotNull(response.getRoles());
                    })
                    .verifyComplete();

            verify(securityUtils, times(1)).extractUserUuidFromJwt();
            verify(userRepository, times(1)).findByUuid("abcd1234");
        }

        @Test
        @DisplayName("User Not Found - UUID from JWT does not exist")
        void getMyProfileFromJwt_UserNotFound() {
            when(securityUtils.extractUserUuidFromJwt()).thenReturn(Mono.just("nonexistent-uuid"));
            when(userRepository.findByUuid("nonexistent-uuid"))
                    .thenReturn(Mono.error(new UserNotFoundException("User not found with UUID: nonexistent-uuid")));

            StepVerifier.create(userMngservice.getMyProfileFromJwt())
                    .expectErrorMatches(error ->
                            error instanceof UserNotFoundException &&
                                    error.getMessage().contains("nonexistent-uuid"))
                    .verify();

            verify(securityUtils, times(1)).extractUserUuidFromJwt();
            verify(userRepository, times(1)).findByUuid("nonexistent-uuid");
        }

        @Test
        @DisplayName("JWT Extraction Error - Invalid or missing JWT")
        void getMyProfileFromJwt_JwtExtractionError() {
            when(securityUtils.extractUserUuidFromJwt())
                    .thenReturn(Mono.error(new RuntimeException("Invalid JWT token")));

            StepVerifier.create(userMngservice.getMyProfileFromJwt())
                    .expectErrorMatches(error ->
                            error instanceof RuntimeException &&
                                    error.getMessage().equals("Invalid JWT token"))
                    .verify();

            verify(securityUtils, times(1)).extractUserUuidFromJwt();
            verify(userRepository, never()).findByUuid(anyString());
        }

        @Test
        @DisplayName("Database Error - Repository throws exception")
        void getMyProfileFromJwt_DatabaseError() {
            when(securityUtils.extractUserUuidFromJwt()).thenReturn(Mono.just("abcd1234"));
            when(userRepository.findByUuid("abcd1234"))
                    .thenReturn(Mono.error(new RuntimeException("Database connection error")));

            StepVerifier.create(userMngservice.getMyProfileFromJwt())
                    .expectErrorMatches(error ->
                            error instanceof RuntimeException &&
                                    error.getMessage().equals("Database connection error"))
                    .verify();

            verify(securityUtils, times(1)).extractUserUuidFromJwt();
            verify(userRepository, times(1)).findByUuid("abcd1234");
        }

        @Test
        @DisplayName("Empty UUID - JWT contains empty UUID")
        void getMyProfileFromJwt_EmptyUuid() {
            when(securityUtils.extractUserUuidFromJwt()).thenReturn(Mono.just(""));
            when(userRepository.findByUuid("")).thenReturn(Mono.empty());

            StepVerifier.create(userMngservice.getMyProfileFromJwt())
                    .expectError(UserNotFoundException.class)
                    .verify();

            verify(securityUtils, times(1)).extractUserUuidFromJwt();
            verify(userRepository, times(1)).findByUuid("");
        }
    }



    @Nested
    @DisplayName("Get User Login By Email Tests")
    class GetUserLoginByEmailTests {

        @Test
        void testGetUserLoginByEmail_Success() {

            User user = new User();
            user.setUsername("testuser");
            user.setEmail("test@test.com");
            user.setPassword("hashedPassword123");

            when(userRepository.findByEmail("test@test.com"))
                    .thenReturn(Mono.just(user));

            StepVerifier.create(userMngservice.getUserLoginByEmail("test@test.com"))
                    .assertNext(response -> {
                        assertEquals("testuser", response.getUsername());
                        assertEquals("test@test.com", response.getEmail());
                        assertNotNull(response.getPassword());
                    })
                    .verifyComplete();
        }

        @Test
        void testGetUserLoginByEmail_EmailNotFound() {

            when(userRepository.findByEmail("nonexistent@test.com"))
                    .thenReturn(Mono.empty());

            StepVerifier.create(userMngservice.getUserLoginByEmail("nonexistent@test.com"))
                    .expectError(EmailNotFoundException.class)
                    .verify();
        }

    }


    @Nested
    @DisplayName("Get User Login By Username Tests")
    class GetUserLoginByUsernameTests {

        @Test
        void testGetUserLoginByUsername_UserNotFound() {

            when(userRepository.findByUsername("nonexistent"))
                    .thenReturn(Mono.empty());

            StepVerifier.create(userMngservice.getUserLoginByUserName("nonexistent"))
                    .expectError(UserNotFoundException.class)
                    .verify();
        }


        @Test
        void testGetUserLoginByUsername_Success() {

            User user = new User();
            user.setUsername("testuser");
            user.setEmail("test@test.com");
            user.setPassword("hashedPassword123");

            when(userRepository.findByUsername("testuser"))
                    .thenReturn(Mono.just(user));

            StepVerifier.create(userMngservice.getUserLoginByUserName("testuser"))
                    .assertNext(response -> {
                        assertEquals("testuser", response.getUsername());
                        assertEquals("test@test.com", response.getEmail());
                        assertNotNull(response.getPassword());
                    })
                    .verifyComplete();
        }


        @Test
        void testGetUserLoginByUsername_DatabaseError() {

            when(userRepository.findByUsername("testuser"))
                    .thenReturn(Mono.error(new RuntimeException("DB connection failed")));

            StepVerifier.create(userMngservice.getUserLoginByUserName("testuser"))
                    .expectError(RuntimeException.class)
                    .verify();
        }

    }


    // --> UPDATE LATEST ACCESS TEST

    @Test
    @DisplayName("updateLatestAccess - Success")
    public void testUpdateLatestAccess_Success(){

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getModifiedCount()).thenReturn(1L);

        when(
            reactiveMongoTemplate.updateFirst(
            any(Query.class),
            any(Update.class),
            eq(User.class)
            )
        )
        .thenReturn(Mono.just(updateResult));

        StepVerifier.create(userMngservice.updateLatestAccess("abcd1234", testLoginTime))
                .verifyComplete();

        verify(reactiveMongoTemplate, times(1))
                .updateFirst(any(Query.class), any(Update.class), eq(User.class));

    }

    @Test
    @DisplayName("updateLatestAccess - User Not Found")
    public void testUpdateLatestAccess_UserNotFound() {

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getModifiedCount()).thenReturn(0L);  // ← NO modifications made

        when(reactiveMongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(User.class)))
                .thenReturn(Mono.just(updateResult));

        StepVerifier.create(userMngservice.updateLatestAccess("nonexistent", testLoginTime))
                .verifyComplete();  // Completa pero loggea warning, no se actualiza nada

        verify(reactiveMongoTemplate, times(1))
                .updateFirst(any(Query.class), any(Update.class), eq(User.class));
    }

}


