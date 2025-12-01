package com.social_media.controller;

import com.social_media.converter.ToEntityConverter;
import com.social_media.converter.UserConverter;
import com.social_media.entity.User;
import com.social_media.model.LoginDto;
import com.social_media.model.TokenDto;
import com.social_media.model.UserDto;
import com.social_media.service.AuthenticationService;
import jakarta.validation.Valid;
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

    private final ToEntityConverter<User, UserDto> userDtoToEntityConverter;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto loginDto) {
        return ResponseEntity.ok(
                authenticationService.login(loginDto.username(), loginDto.password())
        );
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenDto> signup(@RequestBody @Valid UserDto userDto) {
        return new ResponseEntity<>(
                authenticationService.signup(userDtoToEntityConverter.convertToEntity(userDto)),
                HttpStatus.CREATED
        );
    }
}
