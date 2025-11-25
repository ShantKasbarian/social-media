package com.social_media.controller;

import com.social_media.converter.UserConverter;
import com.social_media.model.TokenDto;
import com.social_media.model.UserDto;
import com.social_media.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    private final UserConverter userConverter;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(
                authenticationService.login(userDto.username(), userDto.password())
        );
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenDto> signup(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(
                authenticationService.signup(userConverter.convertToEntity(userDto)),
                HttpStatus.CREATED
        );
    }
}
