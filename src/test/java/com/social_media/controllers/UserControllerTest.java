package com.social_media.controllers;

import com.social_media.converters.UserConverter;
import com.social_media.entities.User;
import com.social_media.models.ResponseDto;
import com.social_media.models.UserDto;
import com.social_media.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserConverter userConverter;

    @Mock
    private Authentication authentication;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("someone@example.com");
        user.setPassword("Password123+");
        user.setUsername("johnDoe");
        user.setName("John");
        user.setLastname("Doe");

        userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getUsername(),
                user.getName(),
                user.getLastname()
        );
    }

    @Test
    void getProfile() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(userConverter.convertToModel(any(User.class))).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.getProfile(authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userDto.id(), response.getBody().id());
    }

    @Test
    void updateUsername() {
        UserDto userDto2 = new UserDto(
                null,
                "someNewEmail@example.com",
                null,
                "someNewUsername",
                null,
                null
        );

        User user2 = new User();
        user2.setId(user.getId());
        user2.setEmail(userDto2.email());
        user2.setPassword(user.getPassword());
        user2.setUsername(userDto2.username());
        user2.setName(user.getName());
        user2.setLastname(user.getLastname());

        when(userConverter.convertToModel(any(User.class))).thenReturn(userDto2);
        when(authentication.getPrincipal()).thenReturn(user);
        when(userService.updateUsername(user, userDto2.username())).thenReturn(user2);

        ResponseEntity<UserDto> response = userController.updateUsername(userDto2, authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotEquals(user.getEmail(), response.getBody().email());
        assertEquals(userDto2.email(), response.getBody().email());
        assertNotEquals(user.getUsername(), response.getBody().username());
        assertEquals(userDto2.username(), response.getBody().username());
    }

    @Test
    void updateEmail() {
        UserDto userDto2 = new UserDto(
                null,
                "someNewEmail@example.com",
                null,
                "someNewUsername",
                null,
                null
        );

        User user2 = new User();
        user2.setId(user.getId());
        user2.setEmail(userDto2.email());
        user2.setPassword(user.getPassword());
        user2.setUsername(userDto2.username());
        user2.setName(user.getName());
        user2.setLastname(user.getLastname());

        when(userConverter.convertToModel(any(User.class))).thenReturn(userDto2);
        when(authentication.getPrincipal()).thenReturn(user);
        when(userService.updateEmail(user, userDto2.email())).thenReturn(user2);

        ResponseEntity<UserDto> response = userController.updateEmail(userDto2, authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotEquals(user.getEmail(), response.getBody().email());
        assertEquals(userDto2.email(), response.getBody().email());
        assertNotEquals(user.getUsername(), response.getBody().username());
        assertEquals(userDto2.username(), response.getBody().username());
    }

    @Test
    void updatePassword() {
        UserDto userDto2 = new UserDto(
                null,
                null,
                "SomeNewPassword123+",
                null,
                null,
                null
        );

        String expected = "password has been changed";

        when(userService.updatePassword(user, userDto2.password())).thenReturn(expected);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<ResponseDto> response = userController.updatePassword(userDto2, authentication);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody().message());
    }

    @Test
    void searchByUsername() {

        UserDto userDto2 = new UserDto(UUID.randomUUID().toString(), "someone2@example.com", null, "SomeUsername2", "someName2", "someLastName2");
        UserDto userDto3 = new UserDto(UUID.randomUUID().toString(), "someone3@example.com", null, "SomeUsername3", "someName3", "someLastName3");

        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setEmail(userDto2.email());
        user2.setPassword(userDto2.password());
        user2.setUsername(userDto2.username());
        user2.setName(userDto2.name());
        user2.setLastname(userDto2.lastname());

        User user3 = new User();
        user3.setId(UUID.randomUUID().toString());
        user3.setEmail(userDto2.email());
        user3.setPassword(userDto2.password());
        user3.setUsername(userDto2.username());
        user3.setName(userDto2.name());
        user3.setLastname(userDto2.lastname());

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        users.add(user3);

        when(userService.searchByUsername("o")).thenReturn(users);
        when(userConverter.convertToModel(user)).thenReturn(userDto);
        when(userConverter.convertToModel(user2)).thenReturn(userDto2);
        when(userConverter.convertToModel(user3)).thenReturn(userDto3);

        ResponseEntity<List<UserDto>> response = userController.searchByUsername("o");

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users.size(), response.getBody().size());
    }
}