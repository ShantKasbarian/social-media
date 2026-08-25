package com.social_media.service.impl;

import com.social_media.entity.User;
import com.social_media.exception.InvalidCredentialsException;
import com.social_media.model.LoginDto;
import com.social_media.model.TokenDto;
import com.social_media.repository.UserRepository;
import com.social_media.service.AuthenticationService;
import com.social_media.service.JwtService;
import com.social_media.utils.CredentialsValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
  private static final String WRONG_USERNAME_OR_PASSWORD_MESSAGE = "wrong username or password";

  private final AuthenticationManager authenticationManager;

  private final UserRepository userRepository;

  private final JwtService jwtService;

  private final CredentialsValidator credentialsValidator;

  private final PasswordEncoder passwordEncoder;

  @Override
  public TokenDto login(LoginDto loginDto) {
    String username = loginDto.username();

    log.info("authenticating user with username {}", username);

    Authentication authentication;

    try {
      authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(username, loginDto.password()));
    } catch (AuthenticationException ex) {
      throw new InvalidCredentialsException(WRONG_USERNAME_OR_PASSWORD_MESSAGE);
    }

    User user = (User) authentication.getPrincipal();
    String token = jwtService.generateToken(username);

    log.info("authenticated user with username {}", username);

    return new TokenDto(token, username, user.getId());
  }

  @Override
  @Transactional
  public TokenDto signup(User user) {
    String username = user.getUsername().trim();

    log.info("registering user with username {}", username);

    credentialsValidator.validateUserCredentials(username, user.getEmail(), user.getPassword());
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    userRepository.save(user);

    String token = jwtService.generateToken(user.getUsername());

    log.info("registered user with username {} and generated auth token", username);

    return new TokenDto(token, user.getUsername(), user.getId());
  }
}
