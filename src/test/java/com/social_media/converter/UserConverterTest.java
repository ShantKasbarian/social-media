package com.social_media.converter;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.User;
import com.social_media.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class UserConverterTest {
  @InjectMocks private UserConverter userConverter;

  private User user;

  private UserDto userDto;

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
  }

  @Test
  void convertToEntity() {
    User user = userConverter.convertToEntity(userDto);

    assertNotNull(user);
    assertEquals(userDto.email(), user.getEmail());
    assertEquals(userDto.username(), user.getUsername());
    assertEquals(userDto.password(), user.getPassword());
    assertEquals(userDto.firstname(), user.getFirstname());
    assertEquals(userDto.lastname(), user.getLastname());
  }

  @Test
  void convertToModel() {
    UserDto userDto = userConverter.convertToModel(user);

    assertNotNull(userDto);
    assertEquals(user.getId(), userDto.id());
    assertEquals(user.getEmail(), userDto.email());
    assertEquals(user.getUsername(), userDto.username());
    assertEquals(user.getPassword(), userDto.password());
    assertEquals(user.getFirstname(), userDto.firstname());
    assertEquals(user.getLastname(), userDto.lastname());
  }
}
