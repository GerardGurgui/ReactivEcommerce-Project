package Ecommerce.Reactive.UserAuthentication_service.UseCases;

import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.RegisterRequestDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.RegistrationResponseDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.UserCreatedResponseDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.model.dto.register.UserRegisterInternalDto;
import Ecommerce.Reactive.UserAuthentication_service.domain.usecase.UserRegistrationUseCase;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.EmailAlreadyExistsException;
import Ecommerce.Reactive.UserAuthentication_service.exceptions.UsernameAlreadyExistsException;
import Ecommerce.Reactive.UserAuthentication_service.service.usermanagement.UserManagementConnectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserRegistrationUseCase
 *
 * Strategy:
 * - Mock PasswordEncoder and UserManagementConnectorService
 * - Test business logic in isolation
 * - Verify password encoding happens before sending to UserManagement
 * - Test error propagation from connector
 * - Validate DTO mapping and field assignment
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRegistrationUseCase - Unit Tests")
public class UserRegistrationUseCaseTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserManagementConnectorService userMngConnector;

    @InjectMocks
    private UserRegistrationUseCase useCase;

    private RegisterRequestDto registerRequestDto;
    private String clientIp;
    private RegistrationResponseDto registrationResponseDto;
    private UserCreatedResponseDto userCreatedResponseDto;

    @BeforeEach
    void setUp() {

        registerRequestDto = RegisterRequestDto.builder()
                .username("testuser")
                .email("testuser@test.com")
                .password("Password@123")
                .name("Test")
                .lastName("User")
                .phone("+1234567890")
                .build();

        clientIp = "1.1";

        registrationResponseDto = RegistrationResponseDto.builder()
                .uuid("uuid1234")
                .username("testuser")
                .email("testuser@test.com")
                .registeredAt(Instant.now())
                .message("User: testuser registered successfully")
                .build();

        userCreatedResponseDto = UserCreatedResponseDto.builder()
                .uuid("uuid1234")
                .username("testuser")
                .email("testuser@test.com")
                .build();

    }


    @Nested
    @DisplayName("register() method tests")
    class RegisterMethodTests {

        @Test
        @DisplayName("Should register user successfully")
        void testRegisterUserSuccessfully() {
            // Arrange
            String hashedPassword = "hashedPassword123";
            Instant registeredTimestamp = Instant.now();

            // ✅ Mock de UserManagement con timestamp
            UserCreatedResponseDto userCreatedResponseDto = UserCreatedResponseDto.builder()
                    .uuid("uuid1234")
                    .username("testuser")
                    .email("testuser@test.com")
                    .registeredAt(registeredTimestamp)  // ✅ UserMng retorna con timestamp
                    .build();

            when(passwordEncoder.encode(registerRequestDto.getPassword()))
                    .thenReturn(hashedPassword);
            when(userMngConnector.createUser(any()))
                    .thenReturn(Mono.just(userCreatedResponseDto));

            // Act & Assert
            StepVerifier.create(useCase.registerUser(registerRequestDto, clientIp))
                    .assertNext(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.getUuid()).isEqualTo("uuid1234");
                        assertThat(response.getUsername()).isEqualTo("testuser");
                        assertThat(response.getEmail()).isEqualTo("testuser@test.com");
                        assertThat(response.getMessage()).isEqualTo("User: testuser registered successfully");
                        assertThat(response.getRegisteredAt()).isNotNull(); //mock from UserMng has timestamp
                        assertThat(response.getRegisteredAt()).isEqualTo(registeredTimestamp);
                    })
                    .verifyComplete();

            verify(passwordEncoder, times(1)).encode(registerRequestDto.getPassword());

            // Verificar DTO enviado a UserManagement
            ArgumentCaptor<UserRegisterInternalDto> captor = ArgumentCaptor.forClass(UserRegisterInternalDto.class);
            verify(userMngConnector).createUser(captor.capture());

            UserRegisterInternalDto capturedDto = captor.getValue();
            assertThat(capturedDto.getUuid()).isNotNull();
            assertThat(capturedDto.getUuid()).hasSize(36);
            assertThat(capturedDto.getUsername()).isEqualTo("testuser");
            assertThat(capturedDto.getName()).isEqualTo("Test");
            assertThat(capturedDto.getLastName()).isEqualTo("User");
            assertThat(capturedDto.getPhone()).isEqualTo("+1234567890");
            assertThat(capturedDto.getEmail()).isEqualTo("testuser@test.com");
            assertThat(capturedDto.getPasswordHash()).isEqualTo(hashedPassword);
            assertThat(capturedDto.getPasswordHash()).isNotEqualTo("Password@123");
            assertThat(capturedDto.getRole()).isEqualTo("USER");
            assertThat(capturedDto.getRegistrationIp()).isEqualTo(clientIp);
            // El DTO enviado NO tiene timestamp (UserAuth no lo genera)
            assertThat(capturedDto.getRegisteredAt()).isNull();


        }

        @Test
        @DisplayName("propagate UsernameAlreadyExistsException when username is taken")
        void testRegister_UsernameAlreadyExists() {

            String hashedPassword = "hashedPassword123";
            //Arrange
            when(passwordEncoder.encode(registerRequestDto.getPassword()))
                    .thenReturn(hashedPassword);
            when(userMngConnector.createUser(any()))
                    .thenReturn(Mono.error(new UsernameAlreadyExistsException("Username 'testuser' already exists")));

            // Act & Assert
            StepVerifier.create(useCase.registerUser(registerRequestDto, clientIp))
                    .expectError(UsernameAlreadyExistsException.class)
            .verify();

            // Verify
            verify(passwordEncoder, times(1)).encode(registerRequestDto.getPassword());
            verify(userMngConnector, times(1)).createUser(any());
        }

        @Test
        @DisplayName("propagate EmailAlreadyExistsException when email is taken")
        void testRegister_EmailAlreadyExists() {

            String hashedPassword = "hashedPassword123";
            //Arrange
            when(passwordEncoder.encode(registerRequestDto.getPassword()))
                    .thenReturn(hashedPassword);
            when(userMngConnector.createUser(any()))
                    .thenReturn(Mono.error(new EmailAlreadyExistsException("Email " + registerRequestDto.getEmail() + " already exists")));

            // Act & Assert
            StepVerifier.create(useCase.registerUser(registerRequestDto, clientIp))
                    .expectError(EmailAlreadyExistsException.class)
                    .verify();

            // Verify
            verify(passwordEncoder, times(1)).encode(registerRequestDto.getPassword());
            verify(userMngConnector, times(1)).createUser(any());
        }
    }

    @Nested
    @DisplayName("Technical Error Tests - Server Errors")
    class TechnicalErrorTests {

        @Test
        @DisplayName("Should propagate ServerErrorException when UserManagement returns 5xx")
        void testRegister_ServerError() {

            // Arrange
            String hashedPassword = "hashedPassword123";

            when(passwordEncoder.encode(registerRequestDto.getPassword()))
                    .thenReturn(hashedPassword);
            when(userMngConnector.createUser(any()))
                    .thenReturn(Mono.error(
                            new ServerErrorException(
                                    "Error during POST request to registerUser: Server error",
                                    new RuntimeException("Internal server error")
                            )
                    ));

            // Act & Assert
            StepVerifier.create(useCase.registerUser(registerRequestDto, clientIp))
                    .expectError(ServerErrorException.class)
                    .verify();

            // Verify
            verify(passwordEncoder, times(1)).encode(registerRequestDto.getPassword());
            verify(userMngConnector, times(1)).createUser(any(UserRegisterInternalDto.class));
        }

        @Test
        @DisplayName("Should propagate RuntimeException for generic communication errors")
        void testRegister_GenericRuntimeException() {

            // Arrange
            String hashedPassword = "hashedPassword123";

            when(passwordEncoder.encode(registerRequestDto.getPassword()))
                    .thenReturn(hashedPassword);
            when(userMngConnector.createUser(any()))
                    .thenReturn(Mono.error(new RuntimeException("Network timeout")));

            // Act & Assert
            StepVerifier.create(useCase.registerUser(registerRequestDto, clientIp))
                    .expectErrorMatches(error ->
                            error instanceof RuntimeException &&
                                    error.getMessage().contains("Network timeout")
                    )
                    .verify();

            // Verify
            verify(passwordEncoder, times(1)).encode(registerRequestDto.getPassword());
            verify(userMngConnector, times(1)).createUser(any(UserRegisterInternalDto.class));
        }

        @Test
        @DisplayName("Should propagate WebClientRequestException for connection errors")
        void testRegister_WebClientRequestException() {

            // Arrange
            String hashedPassword = "hashedPassword123";

            when(passwordEncoder.encode(registerRequestDto.getPassword()))
                    .thenReturn(hashedPassword);
            when(userMngConnector.createUser(any()))
                    .thenReturn(Mono.error(
                            new RuntimeException("Connection refused: UserManagement service unavailable")
                    ));

            // Act & Assert
            StepVerifier.create(useCase.registerUser(registerRequestDto, clientIp))
                    .expectErrorMatches(error ->
                            error instanceof RuntimeException &&
                                    error.getMessage().contains("Connection refused")
                    )
                    .verify();

            // Verify
            verify(passwordEncoder, times(1)).encode(registerRequestDto.getPassword());
            verify(userMngConnector, times(1)).createUser(any(UserRegisterInternalDto.class));
        }
    }

}
