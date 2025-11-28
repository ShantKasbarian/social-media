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

class EmailValidatorImplTest {
    @InjectMocks
    private EmailValidatorImpl emailValidator;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "someone@example.com,true",
            "someone.example.com,false"
    })
    void isEmailValid(String email, boolean isValid) {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        assertEquals(isValid, emailValidator.isEmailValid(email));
    }

    @Test
    void isEmailValidShouldReturnFalseWhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertFalse(emailValidator.isEmailValid("someone@example.com"));
    }
}