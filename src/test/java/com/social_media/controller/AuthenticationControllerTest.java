package com.social_media.controller;

import com.social_media.converter.UserConverter;
import com.social_media.model.ErrorDto;
import com.social_media.model.TokenDto;
import com.social_media.entity.User;
import com.social_media.model.UserDto;
import com.social_media.service.impl.AuthenticationServiceImpl;
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

class AuthenticationControllerTest {
    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationServiceImpl loginSignupService;

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

        when(loginSignupService.login(anyString(), anyString())).thenReturn(new TokenDto(expectedToken, user.getUsername(), user.getId()));

        ResponseEntity<TokenDto> response = authenticationController.login(userDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody().token());
    }

    @Test
    void signup() {
        String expected = "signup successful";

        when(loginSignupService.signup(user)).thenReturn(expected);
        when(userConverter.convertToEntity(userDto)).thenReturn(user);

        ResponseEntity<ErrorDto> response = authenticationController.signup(userDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(expected, response.getBody().message());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}