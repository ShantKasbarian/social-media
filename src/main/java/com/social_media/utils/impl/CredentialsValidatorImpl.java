package com.social_media.utils.impl;

import static com.social_media.utils.PasswordValidator.isPasswordValid;

import com.social_media.exception.InvalidInputException;
import com.social_media.utils.CredentialsValidator;
import com.social_media.utils.EmailValidator;
import com.social_media.utils.UsernameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CredentialsValidatorImpl implements CredentialsValidator {
  private static final String INVALID_EMAIL_MESSAGE = "email is invalid";

  private static final String INVALID_USERNAME_MESSAGE = "username is invalid";

  private static final String INVALID_PASSWORD_MESSAGE =
      "password must be at least 6 characters long, contain at least 1 uppercase, 1 lowercase, 1 number and 1 special character";

  private final UsernameValidator usernameValidator;

  private final EmailValidator emailValidator;

  @Override
  public void validateUserCredentials(String username, String email, String password) {
    if (!usernameValidator.isUsernameValid(username)) {
      throw new InvalidInputException(INVALID_USERNAME_MESSAGE);
    }

    if (!emailValidator.isEmailValid(email)) {
      throw new InvalidInputException(INVALID_EMAIL_MESSAGE);
    }

    if (!isPasswordValid(password)) {
      throw new InvalidInputException(INVALID_PASSWORD_MESSAGE);
    }
  }
}
