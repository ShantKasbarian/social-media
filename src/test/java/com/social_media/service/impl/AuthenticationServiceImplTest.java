package com.social_media.service.impl;

import com.social_media.config.JwtService;
import com.social_media.entity.User;
import com.social_media.exception.InvalidCredentialsException;
import com.social_media.exception.InvalidInputException;
import com.social_media.repository.UserRepository;
import com.social_media.utils.EmailValidator;
import com.social_media.utils.UsernameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {
    private static final String TEST_TOKEN = "some token";

    private static final String INVALID_PASSWORD = "password";

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UsernameValidator usernameValidator;

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

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
    }

    @Test
    void login() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);
        when(jwtService.generateToken(anyString()))
                .thenReturn(TEST_TOKEN);

        var tokenDto = authenticationService.login(user.getUsername(), user.getPassword());

        assertEquals(TEST_TOKEN, tokenDto.token());
        verify(userRepository).findByUsername(anyString());
        verify(jwtService).generateToken(anyString());
    }

    @Test
    void loginShouldThrowInvalidCredentialsExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(user.getUsername(), user.getPassword()));
    }

    @Test
    void loginShouldThrowInvalidCredentialsExceptionWhenPasswordIsWrong() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(user.getEmail(), user.getPassword()));
    }

    @Test
    void signup() {
        String rawPassword = user.getPassword();
        String encodedPassword = "some encoded password";

        when(usernameValidator.isUsernameValid(anyString())).thenReturn(true);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(anyString())).thenReturn(TEST_TOKEN);

        var tokenDto = authenticationService.signup(user);

        assertNotNull(tokenDto);
        assertEquals(TEST_TOKEN, tokenDto.token());
        assertNotEquals(rawPassword, user.getPassword());
        assertEquals(encodedPassword, user.getPassword());
        verify(usernameValidator).isUsernameValid(anyString());
        verify(emailValidator).isEmailValid(anyString());
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(anyString());
    }

    @Test
    void signupShouldThrowInvalidInputExceptionWhenUsernameIsInvalid() {
        when(usernameValidator.isUsernameValid(anyString())).thenReturn(false);
        assertThrows(InvalidInputException.class, () -> authenticationService.signup(user));
    }

    @Test
    void signupShouldThrowInvalidInputExceptionWhenEmailIsInvalid() {
        when(usernameValidator.isUsernameValid(anyString())).thenReturn(true);
        when(emailValidator.isEmailValid(anyString())).thenReturn(false);

        assertThrows(InvalidInputException.class, () -> authenticationService.signup(user));
    }

    @Test
    void signupShouldThrowInvalidInputExceptionWhenPasswordIsInvalid() {
        user.setPassword(INVALID_PASSWORD);

        when(usernameValidator.isUsernameValid(anyString())).thenReturn(true);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);

        assertThrows(InvalidInputException.class, () -> authenticationService.signup(user));
    }
}
