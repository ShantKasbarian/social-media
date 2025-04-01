package com.social_media.controllers;

import com.social_media.converters.UserConverter;
import com.social_media.models.UserDto;
import com.social_media.services.LoginSignupService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class LoginSignupController {
    private final LoginSignupService loginSignupService;

    private final UserConverter userConverter;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(
                loginSignupService.login(userDto.email(), userDto.password())
        );
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(
                loginSignupService.signup(userConverter.convertToEntity(userDto)),
                HttpStatus.CREATED
        );
    }
}
