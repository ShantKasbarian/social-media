package com.social_media.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.User;
import com.social_media.entity.UserBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class UserBlockRepositoryTest {
  @Autowired private UserBlockRepository userBlockRepository;

  @Autowired private UserRepository userRepository;

  private User user;

  private UserBlock userBlock;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setEmail("someone@example.com");
    user.setPassword("Password123+");
    user.setUsername("johnDoe");
    user.setFirstname("John");
    user.setLastname("Doe");

    User user2 = new User();
    user2.setEmail("someone2@example.com");
    user2.setPassword("Password123+");
    user2.setUsername("JackDoe");
    user2.setFirstname("Jack");
    user2.setLastname("Doe");

    userRepository.save(user);
    userRepository.save(user2);

    userBlock = new UserBlock();
    userBlock.setUser(user);
    userBlock.setTargetUser(user2);

    userBlockRepository.save(userBlock);
  }

  @Test
  void existsBlockBetween() {
    boolean response =
        userBlockRepository.existsBlockBetween(
            userBlock.getUser().getId(), userBlock.getTargetUser().getId());

    assertTrue(response);
  }

  @Test
  void findByUserId() {
    var page = userBlockRepository.findByUserId(user.getId(), PageRequest.of(0, 10));

    assertNotNull(page);
    assertFalse(page.isEmpty());
    assertEquals(1, page.getTotalElements());
  }
}
