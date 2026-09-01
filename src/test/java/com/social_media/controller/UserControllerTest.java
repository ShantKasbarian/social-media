package com.social_media.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.social_media.entity.User;
import com.social_media.mapper.UserMapper;
import com.social_media.model.UserDto;
import com.social_media.model.UserPatchDto;
import com.social_media.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

class UserControllerTest {
  @InjectMocks private UserController userController;

  @Mock private UserService userService;

  @Mock private UserMapper userMapper;

  @Mock private Authentication authentication;

  private User user;

  private UserDto userDto;

  private UserPatchDto userPatchDto;

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

    userPatchDto = new UserPatchDto(user.getEmail(), user.getUsername(), user.getPassword());
  }

  @Test
  void getProfile() {
    when(authentication.getPrincipal()).thenReturn(user);
    when(userMapper.toModel(any(User.class))).thenReturn(userDto);

    ResponseEntity<UserDto> response = userController.getProfile(authentication);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(userDto.id(), response.getBody().id());
  }

  @Test
  void updateUser() {
    when(authentication.getPrincipal()).thenReturn(user);
    when(userService.update(any(User.class), any(UserPatchDto.class))).thenReturn(user);

    userController.updateUser(authentication, userPatchDto);

    verify(authentication).getPrincipal();
    verify(userService).update(any(User.class), any(UserPatchDto.class));
  }

  @Test
  void searchByUsername() {
    List<User> users = new ArrayList<>();
    users.add(user);

    Page<User> page = new PageImpl<>(users);

    when(userService.findByUsername(anyString(), any(Pageable.class))).thenReturn(page);

    var response = userController.searchByUsername("o", 0, 10);

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(users.size(), response.getBody().getContent().size());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userService).findByUsername(anyString(), any(Pageable.class));
  }
}
