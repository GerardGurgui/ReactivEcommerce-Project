package Ecommerce.usermanagement.service;

import Ecommerce.usermanagement.document.User;
import Ecommerce.usermanagement.dto.input.UserRegisterInternalDto;
import Ecommerce.usermanagement.exceptions.EmailAlreadyExistsException;
import Ecommerce.usermanagement.exceptions.EmailNotFoundException;
import Ecommerce.usermanagement.exceptions.UserNotFoundException;
import Ecommerce.usermanagement.exceptions.UsernameAlreadyExistsException;
import Ecommerce.usermanagement.repository.IUsersRepository;
import Ecommerce.usermanagement.services.UserManagementService;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
public class UserManagementServiceUnitTest {


    @Mock
    private IUsersRepository userRepository;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @InjectMocks
    private UserManagementService userMngservice;

    private UserRegisterInternalDto userDtoTest;
    private User userTest;
    private List<String> roles;
    private Instant testLoginTime;

    @BeforeEach
    public void setup() {

        userDtoTest = new UserRegisterInternalDto();
        userDtoTest.setUuid("abcd1234");
        userDtoTest.setUsername("testuser");
        userDtoTest.setEmail("testuser@test.com");
        userDtoTest.setRole("USER");

        userTest = new User();
        userTest.setUuid(userDtoTest.getUuid());
        userTest.setUsername(userDtoTest.getUsername());
        userTest.setEmail(userDtoTest.getEmail());

        roles = new ArrayList<>();
        roles.add(userDtoTest.getRole());
        userTest.setRoles(roles);

        testLoginTime = Instant.now();

    }

    @Test
    public void testCreateUser_Success() {

        when(userRepository.existsByUsername(userDtoTest.getUsername())).thenReturn(Mono.just(false));
        when(userRepository.existsByEmail(userDtoTest.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userTest));

        StepVerifier.create(userMngservice.createUser(userDtoTest))
                .assertNext(response -> {
                    assertEquals("abcd1234", response.getUuid());
                    assertEquals("testuser", response.getUsername());
                    assertEquals("testuser@test.com", response.getEmail());
                    assertNotNull(response.getCreatedAt());
                })
                .verifyComplete();

        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("testuser@test.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser_UsernameAlreadyExists(){

        when(userRepository.existsByUsername(userDtoTest.getUsername())).thenReturn(Mono.just(true));

        StepVerifier.create(userMngservice.createUser(userDtoTest))
                .expectError(UsernameAlreadyExistsException.class)
                .verify();

        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void testCreateUser_EmailAlreadyExists(){

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
    public void testCreateUser_DatabaseError() {

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


    // --> GETS


    @Test
    public void testGetUserLoginByUsername_Success() {

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
    public void testGetUserLoginByUsername_UserNotFound() {

        when(userRepository.findByUsername("nonexistent"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userMngservice.getUserLoginByUserName("nonexistent"))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    public void testGetUserLoginByUsername_DatabaseError() {

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Mono.error(new RuntimeException("DB connection failed")));

        StepVerifier.create(userMngservice.getUserLoginByUserName("testuser"))
                .expectError(RuntimeException.class)
                .verify();
    }


    @Test
    public void testGetUserLoginByEmail_Success() {

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
    public void testGetUserLoginByEmail_EmailNotFound() {

        when(userRepository.findByEmail("nonexistent@test.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userMngservice.getUserLoginByEmail("nonexistent@test.com"))
                .expectError(EmailNotFoundException.class)
                .verify();
    }

    @Test
    public void testGetUserLoginByEmail_DatabaseError() {

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Mono.error(new RuntimeException("DB connection failed")));

        StepVerifier.create(userMngservice.getUserLoginByEmail("test@test.com"))
                .expectError(RuntimeException.class)
                .verify();
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


