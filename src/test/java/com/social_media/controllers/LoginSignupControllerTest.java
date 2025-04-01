package com.social_media.controllers;

import com.social_media.converters.UserConverter;
import com.social_media.entities.User;
import com.social_media.models.UserDto;
import com.social_media.services.LoginSignupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class LoginSignupControllerTest {
    @InjectMocks
    private LoginSignupController loginSignupController;

    @Mock
    private LoginSignupService loginSignupService;

    @Mock
    private UserConverter userConverter;

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
    void login() {
        String expectedToken = "some token";

        when(loginSignupService.login(anyString(), anyString())).thenReturn(expectedToken);

        ResponseEntity<String> response = loginSignupController.login(userDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
    }

    @Test
    void signup() {
        String expected = "signup successful";

        when(loginSignupService.signup(user)).thenReturn(expected);
        when(userConverter.convertToEntity(userDto)).thenReturn(user);

        ResponseEntity<String> response = loginSignupController.signup(userDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(expected, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}