package com.social_media.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.social_media.entity.User;
import com.social_media.model.UserDto;
import com.social_media.model.UserPatchDto;
import com.social_media.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceImplTest {
  @InjectMocks private UserServiceImpl userService;

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  private User user;

  private UserDto userDto;

  private UserPatchDto userPatchDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("someone@example.com");
    user.setPassword("Password123+");
    user.setUsername("johnDoe");
    user.setFirstname("John");
    user.setLastname("Doe");

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
  void update() {
    when(passwordEncoder.encode(anyString())).thenReturn(user.getPassword());

    userService.update(user, userPatchDto);

    verify(passwordEncoder).encode(anyString());
  }

  @Test
  void findByUsername() {
    List<User> users = new ArrayList<>();
    users.add(user);

    Page<User> page = new PageImpl<>(users);
    Pageable pageable = PageRequest.of(0, 10);

    when(userRepository.findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class)))
        .thenReturn(page);

    var response = userService.findByUsername(user.getUsername(), pageable);

    assertNotNull(response);
    assertEquals(page, response);
    verify(userRepository).findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class));
  }
}
