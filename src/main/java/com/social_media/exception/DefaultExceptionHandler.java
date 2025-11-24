package com.social_media.exception;

import com.social_media.model.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseDto> handle(InvalidCredentialsException e) {
        return new ResponseEntity<>(
                new ResponseDto(
                    e.getMessage()
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidProvidedInfoException.class)
    public ResponseEntity<ResponseDto> handle(InvalidProvidedInfoException e) {
        return new ResponseEntity<>(
                new ResponseDto(
                    e.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RequestNotAllowedException.class)
    public ResponseEntity<ResponseDto> handle(RequestNotAllowedException e) {
        return new ResponseEntity<>(
                new ResponseDto(
                    e.getMessage()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto> handle(ResourceNotFoundException e) {
        return new ResponseEntity<>(
                new ResponseDto(
                    e.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ResponseDto> handle(ResourceAlreadyExistsException e) {
        return new ResponseEntity<>(
                new ResponseDto(
                    e.getMessage()
                ),
                HttpStatus.CONFLICT
        );
    }
}
