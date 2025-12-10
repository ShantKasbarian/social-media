package com.social_media.utils.impl;

import com.social_media.exception.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CredentialsValidatorImplTest {
    private static final String USERNAME = "johnDoe";

    private static final String EMAIL = "someone@example.com";

    private static final String PASSWORD = "Password123+";

    private static final String INVALID_PASSWORD = "password";

    @InjectMocks
    private CredentialsValidatorImpl credentialsValidator;

    @Mock
    private UsernameValidatorImpl usernameValidator;

    @Mock
    private EmailValidatorImpl emailValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateUserCredentials() {
        when(usernameValidator.isUsernameValid(anyString())).thenReturn(true);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);

        assertDoesNotThrow(() -> credentialsValidator.validateUserCredentials(USERNAME, EMAIL, PASSWORD));
        verify(usernameValidator).isUsernameValid(anyString());
        verify(emailValidator).isEmailValid(anyString());
    }

    @Test
    void validateUserCredentialsShouldThrowInvalidInputExceptionWhenUsernameIsInvalid() {
        when(usernameValidator.isUsernameValid(anyString())).thenReturn(false);
        assertThrows(InvalidInputException.class, () -> credentialsValidator.validateUserCredentials(USERNAME, EMAIL, PASSWORD));
    }

    @Test
    void validateUserCredentialsShouldThrowInvalidInputExceptionWhenEmailIsInvalid() {
        when(usernameValidator.isUsernameValid(anyString())).thenReturn(true);
        when(emailValidator.isEmailValid(anyString())).thenReturn(false);

        assertThrows(InvalidInputException.class, () -> credentialsValidator.validateUserCredentials(USERNAME, EMAIL, PASSWORD));
    }

    @Test
    void validateUserCredentialsShouldThrowInvalidInputExceptionWhenPasswordIsInvalid() {
        when(usernameValidator.isUsernameValid(anyString())).thenReturn(true);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);

        assertThrows(InvalidInputException.class, () -> credentialsValidator.validateUserCredentials(USERNAME, EMAIL, INVALID_PASSWORD));
    }
}
