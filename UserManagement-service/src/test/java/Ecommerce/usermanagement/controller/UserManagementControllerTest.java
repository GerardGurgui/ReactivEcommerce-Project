package Ecommerce.usermanagement.controller;


import Ecommerce.usermanagement.dto.input.UserRegisterDto;
import Ecommerce.usermanagement.dto.output.UserProfileDto;
import Ecommerce.usermanagement.dto.output.UserOwnProfileDto;
import Ecommerce.usermanagement.dto.output.UserInfoOutputDto;
import Ecommerce.usermanagement.exceptions.UserNotFoundException;
import Ecommerce.usermanagement.repository.IUsersRepository;
import Ecommerce.usermanagement.security.config.InternalServiceAuthFilter;
import Ecommerce.usermanagement.services.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@WebFluxTest(
        controllers = UserManagementController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveOAuth2ResourceServerAutoConfiguration.class
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = InternalServiceAuthFilter.class
        )
)
public class UserManagementControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserManagementService userMngService;

    @MockBean
    private IUsersRepository userRepository;

    // basic user info, public profile
    private UserProfileDto userProfileDtoTest;
    // detailed user info, only own user and admin
    private UserOwnProfileDto userOwnProfileDtoTest;
    // expected created user
    private UserOwnProfileDto expectedUserCreated;

    // all user info, only admin
    private List<UserInfoOutputDto> listUsersinfoTest;
    private UserInfoOutputDto userInfoDtoTest1;
    private UserInfoOutputDto userInfoDtoTest2;
    private UserRegisterDto userRegisterDtoTest;

    private List<String> roles;
    private static final String CONTROLLER_BASE_URL = "/api/usermanagement";

    @BeforeEach
    public void setup() {

        //Basic user info, public profile
        userProfileDtoTest = UserProfileDto.builder()
                .username("testuserProfile")
                .email("testUserProfile@test.com")
                .name("testnameUserProfile")
                .build();

        //Detailed own user profile
        userOwnProfileDtoTest = UserOwnProfileDto.builder()
                .uuid("myprofileuuid")
                .username("myprofileuser")
                .name("my profile name")
                .email("testprofile@test.com")
                .phone("1234567890")
                .roles(roles)
                .registeredAt(Instant.now())
                .build();

        //List of all users info, only for admin
        roles = List.of("USER");
        userInfoDtoTest1 = UserInfoOutputDto.builder()
                .username("testuser")
                .email("testuser@test.com")
                .name("testname")
                .phone("1234567890")
                .roles(roles)
                .build();

        userInfoDtoTest2 = UserInfoOutputDto.builder()
                .username("testuser2")
                .email("testuser2@test.com")
                .name("testname2")
                .phone("0987654321")
                .roles(roles)
                .build();
        listUsersinfoTest = List.of(userInfoDtoTest1, userInfoDtoTest2);

        //new user register dto
        userRegisterDtoTest = UserRegisterDto.builder()
                .uuid("newuseruuid")
                .username("newuser")
                .email("newUserTest@test.com")
                .passwordHash("hashedpassword123")
                .name("New User")
                .lastName("Test")
                .phone("1122334455")
                .registrationIp("1")
                .registeredAt(Instant.now())
                .role("USER")
                .build();

        expectedUserCreated = UserOwnProfileDto.builder()
                .uuid(userRegisterDtoTest.getUuid())              // "newuseruuid"
                .username(userRegisterDtoTest.getUsername())      // "newuser"
                .name(userRegisterDtoTest.getName())              // "New User"
                .email(userRegisterDtoTest.getEmail())            // "newUserTest@test.com"
                .phone(userRegisterDtoTest.getPhone())            // "1122334455"
                .roles(List.of(userRegisterDtoTest.getRole()))    // ["USER"]
                .registeredAt(userRegisterDtoTest.getRegisteredAt())
                .build();
    }

    @Test
    @DisplayName("GET own profile /users/me - Success")
    void testGetMyProfile_Success() {

        when(userMngService.getMyProfileFromJwt()).thenReturn(
                Mono.just(userOwnProfileDtoTest)
        );

        webTestClient.get()
                .uri(CONTROLLER_BASE_URL + "/users/me")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserOwnProfileDto.class)
                .value(response -> {
                    assertEquals("myprofileuuid", response.getUuid());
                    assertEquals("myprofileuser", response.getUsername());
                    assertEquals("my profile name", response.getName());
                    assertEquals("testprofile@test.com", response.getEmail());
                    assertEquals("1234567890", response.getPhone());
                });

        verify(userMngService, times(1)).getMyProfileFromJwt();
    }

    @Test
    @DisplayName("GET own profile /users/me - Not Found")
    void testGetMyProfile_NotFound() {

        when(userMngService.getMyProfileFromJwt())
                .thenReturn(Mono.error(new UserNotFoundException("User not found")));

        webTestClient.get()
                .uri(CONTROLLER_BASE_URL + "/users/me")
                .exchange()
                .expectStatus().isNotFound();

        verify(userMngService, times(1)).getMyProfileFromJwt();
    }


    @Test
    @DisplayName("GET public profile /users/uuid/{uuid} - Success")
    void testGetUserProfileByUuid_Success() {

        when(userMngService.getUserProfile("test-uuid-1234")).thenReturn(
                Mono.just(userProfileDtoTest));

        webTestClient.get()
                .uri(CONTROLLER_BASE_URL + "/users/uuid/test-uuid-1234")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserProfileDto.class)
                .value(userInfoResponse -> {
                    assertEquals("testuserProfile", userInfoResponse.getUsername());
                    assertEquals("testUserProfile@test.com", userInfoResponse.getEmail());
                    assertEquals("testnameUserProfile", userInfoResponse.getName());
                });
        verify(userMngService, times(1)).getUserProfile("test-uuid-1234");

    }

    @Test
    @DisplayName("GET public profile /users/uuid/{uuid} - Not Found")
    void testGetUserProfileByUuid_NotFound() {

        when(userMngService.getUserProfile("noExists"))
                .thenReturn(Mono.error(new UserNotFoundException("User with Uuid: noExists not found")));

        webTestClient.get()
                .uri(CONTROLLER_BASE_URL + "/users/uuid/noExists" )
                .exchange()
                .expectStatus().isNotFound();

        verify(userMngService, times(1)).getUserProfile("noExists");
    }


    @Test
    @DisplayName("GET public profile by username /users/username/{username} - Success")
    void testGetUserProfileByUsername_Success() {

        when(userMngService.getUserByUserName("testuserProfile")).thenReturn(
                Mono.just(userProfileDtoTest));

        webTestClient.get()
                .uri(CONTROLLER_BASE_URL + "/users/username/testuserProfile")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserProfileDto.class)
                .value(userInfoResponse -> {
                    assertEquals("testuserProfile", userInfoResponse.getUsername());
                    assertEquals("testUserProfile@test.com", userInfoResponse.getEmail());
                    assertEquals("testnameUserProfile", userInfoResponse.getName());
                });
        verify(userMngService, times(1)).getUserByUserName("testuserProfile");
    }


    @Test
    @DisplayName("GET all users info /users/list - Success")
    void testGetAllUsersInfo_Success() {

        when(userMngService.getAllUsersInfo()).thenReturn(
                Mono.just(listUsersinfoTest)
        );

        webTestClient.get()
                .uri(CONTROLLER_BASE_URL + "/users/list")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserInfoOutputDto.class)
                .value(responseList -> {
                    assertEquals(2, responseList.size());
                    assertEquals("testuser", responseList.get(0).getUsername());
                    assertEquals("testuser2", responseList.get(1).getUsername());
                    assertNotNull(responseList.get(0).getRoles());
                    assertNotNull(responseList.get(1).getRoles());
                });

        verify(userMngService, times(1)).getAllUsersInfo();
    }

    // INTERNAL USE ONLY - Create and save new user

    @Nested
    @DisplayName("User Creation Tests - POST - /internal/addUser")
    class UserCreationTests {

        @Test
        @DisplayName("Success")
        void testCreateUser_Success() {

            when(userMngService.createUser(any(UserRegisterDto.class))).thenReturn(
                    Mono.just(expectedUserCreated)
            );

            webTestClient.post()
                    .uri(CONTROLLER_BASE_URL + "/internal/addUser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(userRegisterDtoTest)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(UserOwnProfileDto.class)
                    .value(createdUserResponse -> {
                        assertEquals("newuseruuid", createdUserResponse.getUuid());
                        assertEquals("newuser", createdUserResponse.getUsername());
                        assertEquals("New User", createdUserResponse.getName());
                        assertEquals("newUserTest@test.com", createdUserResponse.getEmail());
                        assertEquals("1122334455", createdUserResponse.getPhone());
                        assertEquals(1, createdUserResponse.getRoles().size());
                        assertEquals("USER", createdUserResponse.getRoles().get(0));
                        assertNotNull(createdUserResponse.getRegisteredAt());
                    });

            verify(userMngService, times(1)).createUser(any(UserRegisterDto.class));
        }

        @Test
        @DisplayName("Error")
        void testCreateUser_Error() {

            when(userMngService.createUser(any(UserRegisterDto.class)))
                    .thenReturn(Mono.error(new RuntimeException("Internal Server Error")));

            webTestClient.post()
                    .uri(CONTROLLER_BASE_URL + "/internal/addUser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(userRegisterDtoTest)
                    .exchange()
                    .expectStatus().is5xxServerError();

            verify(userMngService, times(1)).createUser(any(UserRegisterDto.class));
        }
    }

}
