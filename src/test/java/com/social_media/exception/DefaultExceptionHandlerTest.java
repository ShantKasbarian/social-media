package com.social_media.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.social_media.exception.DefaultExceptionHandler.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultExceptionHandlerTest {
    @InjectMocks
    private DefaultExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleInvalidCredentialsException() {
        var response = exceptionHandler.handle(new InvalidCredentialsException(""));

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(INVALID_CREDENTIALS_MESSAGE, response.getBody().error());
    }

    @Test
    void handleInvalidInputException() {
        var response = exceptionHandler.handle(new InvalidInputException(""));

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(INVALID_INPUT_MESSAGE, response.getBody().error());
    }

    @Test
    void handleRequestNotAllowedException() {
        var response = exceptionHandler.handle(new RequestNotAllowedException(""));

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(REQUEST_NOT_ALLOWED_MESSAGE, response.getBody().error());
    }

    @Test
    void handleResourceNotFoundException() {
        var response = exceptionHandler.handle(new ResourceNotFoundException(""));

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(RESOURCE_NOT_FOUND_MESSAGE, response.getBody().error());
    }

    @Test
    void handleNoResourceFoundException() {
        var response = exceptionHandler.handle(new NoResourceFoundException(HttpMethod.GET, ""));

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(RESOURCE_NOT_FOUND_MESSAGE, response.getBody().error());
    }

    @Test
    void handleResourceAlreadyExistsException() {
        var response = exceptionHandler.handle(new ResourceAlreadyExistsException(""));

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(RESOURCE_ALREADY_EXISTS_MESSAGE, response.getBody().error());
    }

    @Test
    void handleInternalServerError() {
        var response = exceptionHandler.handle(new Throwable());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(INTERNAL_SERVER_ERROR_MESSAGE, response.getBody().error());
    }
}
