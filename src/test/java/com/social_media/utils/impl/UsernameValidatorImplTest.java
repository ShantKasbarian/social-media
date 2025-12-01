package com.social_media.utils.impl;

import com.social_media.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UsernameValidatorImplTest {
    @InjectMocks
    private UsernameValidatorImpl usernameValidator;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "John.Doe,true",
            "John Doe,false"
    })
    void isUsernameValid(String username, boolean isValid) {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        assertEquals(isValid, usernameValidator.isUsernameValid(username));
    }

    @Test
    void isUsernameValidShouldReturnFalseWhenUsernameExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        assertFalse(usernameValidator.isUsernameValid("John.Doe"));
    }
}
