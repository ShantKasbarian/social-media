package com.social_media.controller;

import com.social_media.converter.UserConverter;
import com.social_media.model.LoginDto;
import com.social_media.model.TokenDto;
import com.social_media.entity.User;
import com.social_media.model.UserDto;
import com.social_media.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {
    private static final String TEST_TOKEN = "test token";

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserConverter userConverter;

    private User user;

    private UserDto userDto;

    private LoginDto loginDto;

    private TokenDto tokenDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("someone@example.com");
        user.setPassword("Password123+");
        user.setUsername("johnDoe");
        user.setFirstname("John");
        user.setLastname("Doe");

        userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname()
        );

        loginDto = new LoginDto(user.getUsername(), user.getPassword());

        tokenDto = new TokenDto(TEST_TOKEN, user.getUsername(), user.getId());
    }

    @Test
    void login() {
        when(authenticationService.login(anyString(), anyString())).thenReturn(tokenDto);

        var response = authenticationController.login(loginDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(tokenDto, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationService).login(anyString(), anyString());
    }

    @Test
    void signup() {
        when(authenticationService.signup(user)).thenReturn(tokenDto);
        when(userConverter.convertToEntity(any(UserDto.class))).thenReturn(user);

        var response = authenticationController.signup(userDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(tokenDto, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authenticationService).signup(any(User.class));
        verify(userConverter).convertToEntity(any(UserDto.class));
    }
}
