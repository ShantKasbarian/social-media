package com.social_media.exception;

import com.social_media.model.ErrorDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@ControllerAdvice
public class DefaultExceptionHandler {
    static final String INVALID_CREDENTIALS_MESSAGE = "invalid credentials";

    static final String INVALID_INPUT_MESSAGE = "invalid input";

    static final String REQUEST_NOT_ALLOWED_MESSAGE = "request not allowed";

    static final String RESOURCE_NOT_FOUND_MESSAGE = "resource not found";

    static final String RESOURCE_ALREADY_EXISTS_MESSAGE = "resource already exists";

    static final String INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected error occurred. Please try again later.";

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDto> handle(InvalidCredentialsException e) {
        return new ResponseEntity<>(
                new ErrorDto(INVALID_CREDENTIALS_MESSAGE), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorDto> handle(InvalidInputException e) {
        return new ResponseEntity<>(
                new ErrorDto(INVALID_INPUT_MESSAGE), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RequestNotAllowedException.class)
    public ResponseEntity<ErrorDto> handle(RequestNotAllowedException e) {
        return new ResponseEntity<>(
                new ErrorDto(REQUEST_NOT_ALLOWED_MESSAGE), HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorDto> handle(Exception e) {
        return new ResponseEntity<>(
                new ErrorDto(RESOURCE_NOT_FOUND_MESSAGE), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handle(ResourceAlreadyExistsException e) {
        return new ResponseEntity<>(
                new ErrorDto(RESOURCE_ALREADY_EXISTS_MESSAGE), HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handle(MethodArgumentNotValidException e) {
        List<String> errorMessages = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return new ResponseEntity<>(
                new ErrorDto(errorMessages.toString()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handle(ConstraintViolationException e) {
        var errors = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessageTemplate)
                .toList();

        return new ResponseEntity<>(
                new ErrorDto(errors.toString()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorDto> handle(Throwable e) {
        System.err.println(e.getMessage());
        e.printStackTrace();

        return new ResponseEntity<>(
                new ErrorDto(INTERNAL_SERVER_ERROR_MESSAGE),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
