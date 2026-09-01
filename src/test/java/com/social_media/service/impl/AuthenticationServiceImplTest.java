package com.social_media.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.social_media.entity.User;
import com.social_media.exception.InvalidCredentialsException;
import com.social_media.model.LoginDto;
import com.social_media.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthenticationServiceImplTest {
  private static final String TEST_TOKEN = "some token";

  @InjectMocks private AuthenticationServiceImpl authenticationService;

  @Mock private AuthenticationManager authenticationManager;

  @Mock private UserRepository userRepository;

  @Mock private JwtServiceImpl jwtService;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private Authentication authentication;

  private User user;

  private LoginDto loginDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    user.setId(UUID.randomUUID());

    loginDto = new LoginDto(user.getUsername(), user.getPassword());

    when(authentication.getPrincipal()).thenReturn(user);
  }

  @Test
  void login() {
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(jwtService.generateToken(anyString())).thenReturn(TEST_TOKEN);

    var tokenDto = authenticationService.login(loginDto);

    assertEquals(TEST_TOKEN, tokenDto.token());
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService).generateToken(anyString());
  }

  @Test
  void
      loginShouldThrowInvalidCredentialsExceptionWhenAnySubClassOfAuthenticationExceptionIsThrown() {
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(BadCredentialsException.class);
    assertThrows(InvalidCredentialsException.class, () -> authenticationService.login(loginDto));
  }

  @Test
  void signup() {
    String rawPassword = user.getPassword();
    String encodedPassword = "some encoded password";

    when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(jwtService.generateToken(anyString())).thenReturn(TEST_TOKEN);

    var tokenDto = authenticationService.signup(user);

    assertNotNull(tokenDto);
    assertEquals(TEST_TOKEN, tokenDto.token());
    assertNotEquals(rawPassword, user.getPassword());
    assertEquals(encodedPassword, user.getPassword());
    verify(passwordEncoder).encode(anyString());
    verify(userRepository).save(any(User.class));
    verify(jwtService).generateToken(anyString());
  }
}
