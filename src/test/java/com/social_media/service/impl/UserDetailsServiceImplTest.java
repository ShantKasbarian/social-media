package com.social_media.service.impl;

import com.social_media.entity.User;
import com.social_media.exception.InvalidCredentialsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

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
    void loadUserByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(user));

        var response = userDetailsService.loadUserByUsername(user.getUsername());

        assertNotNull(response);
        assertEquals(user, response);
        verify(userRepository).findByUsername(anyString());
    }

    @Test
    void loadUserByUsernameShouldThrowInvalidCredentialsExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> userDetailsService.loadUserByUsername(user.getUsername()));
    }
}
