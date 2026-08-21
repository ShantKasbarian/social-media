package com.social_media.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PasswordValidatorTest {
  @ParameterizedTest
  @CsvSource({
    "Password123, false",
    "password123, false",
    "Password+, false",
    "PASSWORD123+, false",
    "Password123+, true"
  })
  void isPasswordValid(String password, boolean isValid) {
    assertEquals(isValid, PasswordValidator.isPasswordValid(password));
  }
}
