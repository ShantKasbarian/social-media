package com.social_media.controller;

import com.social_media.converter.UserConverter;
import com.social_media.entity.User;
import com.social_media.model.LoginDto;
import com.social_media.model.TokenDto;
import com.social_media.model.UserDto;
import com.social_media.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  private final UserConverter userConverter;

  @PostMapping("/login")
  public ResponseEntity<TokenDto> login(@RequestBody @Valid LoginDto loginDto) {
    TokenDto tokenDto = authenticationService.login(loginDto);
    return ResponseEntity.ok(tokenDto);
  }

  @PostMapping("/signup")
  public ResponseEntity<TokenDto> signup(@RequestBody @Valid UserDto userDto) {
    User user = userConverter.convertToEntity(userDto);

    TokenDto tokenDto = authenticationService.signup(user);

    return new ResponseEntity<>(tokenDto, HttpStatus.CREATED);
  }
}
