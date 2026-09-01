package com.social_media.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.social_media.entity.User;
import com.social_media.mapper.UserMapper;
import com.social_media.model.LoginDto;
import com.social_media.model.TokenDto;
import com.social_media.model.UserDto;
import com.social_media.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class AuthenticationControllerTest {
  private static final String TEST_TOKEN = "test token";

  @InjectMocks private AuthenticationController authenticationController;

  @Mock private AuthenticationService authenticationService;

  @Mock private UserMapper userMapper;

  private User user;

  private UserDto userDto;

  private LoginDto loginDto;

  private TokenDto tokenDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");

    userDto =
        new UserDto(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            user.getUsername(),
            user.getFirstname(),
            user.getLastname());

    loginDto = new LoginDto(user.getUsername(), user.getPassword());

    tokenDto = new TokenDto(TEST_TOKEN, user.getUsername(), user.getId());
  }

  @Test
  void login() {
    when(authenticationService.login(any(LoginDto.class))).thenReturn(tokenDto);

    var response = authenticationController.login(loginDto);

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(tokenDto, response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(authenticationService).login(any(LoginDto.class));
  }

  @Test
  void signup() {
    when(authenticationService.signup(user)).thenReturn(tokenDto);
    when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);

    var response = authenticationController.signup(userDto);

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(tokenDto, response.getBody());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    verify(authenticationService).signup(any(User.class));
    verify(userMapper).toEntity(any(UserDto.class));
  }
}
