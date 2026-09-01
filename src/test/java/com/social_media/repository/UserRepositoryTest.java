package com.social_media.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
class UserRepositoryTest {
  @Autowired private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    userRepository.save(user);
  }

  @Test
  void findByUsername() {
    User response = userRepository.findByUsername(user.getUsername()).get();

    assertNotNull(response);
    assertEquals(user.getId(), response.getId());
    assertEquals(user.getEmail(), response.getEmail());
  }

  @Test
  void existsByEmail() {
    assertTrue(userRepository.existsByEmail(user.getEmail()));
  }

  @Test
  void existsByUsername() {
    assertTrue(userRepository.existsByUsername(user.getUsername()));
  }

  @Test
  void findByUsernameContainingIgnoreCase() {
    String usernameFirstLetter = user.getUsername().charAt(0) + "";
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("username")));

    Page<User> response =
        userRepository.findByUsernameContainingIgnoreCase(usernameFirstLetter, pageable);

    assertNotNull(response);
    assertFalse(response.isEmpty());
    assertEquals(user.getId(), response.getContent().getFirst().getId());
    assertEquals(user.getEmail(), response.getContent().getFirst().getEmail());
  }
}
