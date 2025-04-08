package com.social_media.services;

import com.social_media.config.JwtService;
import com.social_media.entities.User;
import com.social_media.exceptions.InvalidCredentialsException;
import com.social_media.exceptions.InvalidProvidedInfoException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.models.TokenDto;
import com.social_media.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginSignupServiceTest {
    @InjectMocks
    private LoginSignupService loginSignupService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private User user;

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
    }

    @Test
    void login() {
        String token = "some token";
        String rawPassword = "Password123+";
        user.setPassword(bCryptPasswordEncoder.encode(rawPassword));

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.ofNullable(user));
        when(jwtService.generateToken(user.getUsername()))
                .thenReturn(token);
        when(bCryptPasswordEncoder.matches(rawPassword, user.getPassword()))
                .thenReturn(true);

        TokenDto response = loginSignupService.login(user.getEmail(), rawPassword);

        assertEquals(token, response.token());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(jwtService, times(1)).generateToken(user.getUsername());
    }

    @Test
    void loginShouldThrowInvalidCredentialsExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenThrow(InvalidCredentialsException.class);

        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.login(user.getEmail(), user.getPassword()));
    }

    @Test
    void loginShouldThrowInvalidCredentialsExceptionWhenPasswordIsWrong() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.ofNullable(user));
        String rawPassword = "Password123+";
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);

        when(bCryptPasswordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () -> loginSignupService.login(user.getEmail(), user.getPassword()));
    }

    @Test
    void signup() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        String rawPassword = "Password123+";
        String response = loginSignupService.signup(user);

        assertEquals("signup successful", response);
        assertNotEquals(rawPassword, user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void signupShouldThrowResourceAlreadyExistsExceptionWhenUserWithEmailIsFound() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> loginSignupService.signup(user));
    }

    @Test
    void signupShouldThrowResourceAlreadyExistsExceptionWhenUserWithUsernameIsFound() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> loginSignupService.signup(user));
    }

    @Test
    void signupShouldThrowInvalidProvidedInfoExceptionWhenPasswordIsInvalid() {
        user.setPassword("password");
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        assertThrows(InvalidProvidedInfoException.class, () -> loginSignupService.signup(user));
    }


    @ParameterizedTest
    @CsvSource({
            "Password123, false",
            "password123, false",
            "Password+, false",
            "PASSWORD123+, false",
            "Password123+, true"
    })
    void isPasswordValid(String password, boolean expected) {
        assertEquals(expected, loginSignupService.isPasswordValid(password));
    }
}