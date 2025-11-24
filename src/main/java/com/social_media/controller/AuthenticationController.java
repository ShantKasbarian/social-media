package com.social_media.controller;

import com.social_media.converter.UserConverter;
import com.social_media.model.ResponseDto;
import com.social_media.model.TokenDto;
import com.social_media.model.UserDto;
import com.social_media.service.impl.AuthenticationServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class AuthenticationController {
    private final AuthenticationServiceImpl loginSignupService;

    private final UserConverter userConverter;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(
                loginSignupService.login(userDto.email(), userDto.password())
        );
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signup(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(
                new ResponseDto(
                    loginSignupService.signup(userConverter.convertToEntity(userDto))
                ), HttpStatus.CREATED
        );
    }
}
