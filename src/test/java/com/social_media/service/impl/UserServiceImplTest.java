package com.social_media.service.impl;

import com.social_media.entity.User;
import com.social_media.exception.InvalidInputException;
import com.social_media.repository.UserRepository;
import com.social_media.utils.EmailValidator;
import com.social_media.utils.UsernameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

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
    void updateUser() {
        when(usernameValidator.isUsernameValid(anyString())).thenReturn(true);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn(user.getPassword());

        userService.updateUser(user, user);

        verify(usernameValidator).isUsernameValid(anyString());
        verify(emailValidator).isEmailValid(anyString());
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserShouldThrowInvalidInputExceptionWhenUsernameIsInvalid() {
        when(usernameValidator.isUsernameValid(anyString())).thenReturn(false);
        assertThrows(InvalidInputException.class, () -> userService.updateUser(user, user));
    }

    @Test
    void updateUserShouldThrowInvalidInputExceptionWhenEmailIsInvalid() {
        when(usernameValidator.isUsernameValid(anyString())).thenReturn(true);
        when(emailValidator.isEmailValid(anyString())).thenReturn(false);
        assertThrows(InvalidInputException.class, () -> userService.updateUser(user, user));
    }

    @Test
    void updateUserShouldThrowInvalidInputExceptionWhenPasswordIsInvalid() {
        String invalidPassword = "password";
        user.setPassword(invalidPassword);

        when(usernameValidator.isUsernameValid(anyString())).thenReturn(true);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);
        assertThrows(InvalidInputException.class, () -> userService.updateUser(user, user));
    }

    @Test
    void searchByUsername() {
        List<User> users = new ArrayList<>();
        users.add(user);

        Page<User> page = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(page);

        var response = userService.searchByUsername(user.getUsername(), pageable);

        assertNotNull(response);
        assertEquals(page, response);
        verify(userRepository).findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class));
    }
}
