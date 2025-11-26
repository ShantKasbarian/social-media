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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class DefaultExceptionHandler {
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected error occurred. Please try again later.";

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDto> handle(InvalidCredentialsException e) {
        return new ResponseEntity<>(
                new ErrorDto(e.getMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidProvidedInfoException.class)
    public ResponseEntity<ErrorDto> handle(InvalidProvidedInfoException e) {
        return new ResponseEntity<>(
                new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RequestNotAllowedException.class)
    public ResponseEntity<ErrorDto> handle(RequestNotAllowedException e) {
        return new ResponseEntity<>(
                new ErrorDto(e.getMessage()), HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handle(ResourceNotFoundException e) {
        return new ResponseEntity<>(
                new ErrorDto(e.getMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handle(ResourceAlreadyExistsException e) {
        return new ResponseEntity<>(
                new ErrorDto(e.getMessage()), HttpStatus.CONFLICT
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
    public ResponseEntity<ErrorDto> handleConstraintViolation(ConstraintViolationException e) {
        var errors = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessageTemplate)
                .toList();

        return new ResponseEntity<>(
                new ErrorDto(errors.toString()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handle(Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace();

        return new ResponseEntity<>(
                new ErrorDto(INTERNAL_SERVER_ERROR_MESSAGE),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
