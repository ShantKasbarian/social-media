package com.social_media.controller;

import com.social_media.converter.ToEntityConverter;
import com.social_media.entity.User;
import com.social_media.model.LoginDto;
import com.social_media.model.TokenDto;
import com.social_media.model.UserDto;
import com.social_media.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    private final ToEntityConverter<User, UserDto> userDtoToEntityConverter;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto loginDto) {
        String username = loginDto.username();

        log.info("/login with POST called, authenticating user with username {}", username);

        TokenDto tokenDto = authenticationService.login(username, loginDto.password());

        log.info("authenticated user with username {}", username);

        return ResponseEntity.ok(tokenDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenDto> signup(@RequestBody @Valid UserDto userDto) {
        String username = userDto.username();

        log.info("/signup with POST called creating user with username {}", username);

        User user = userDtoToEntityConverter.convertToEntity(userDto);

        TokenDto tokenDto = authenticationService.signup(user);

        log.info("created user with username {}", username);

        return new ResponseEntity<>(tokenDto, HttpStatus.CREATED);
    }
}
