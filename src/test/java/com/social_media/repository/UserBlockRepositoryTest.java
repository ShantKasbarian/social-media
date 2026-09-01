package com.social_media.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.User;
import com.social_media.entity.UserBlock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class UserBlockRepositoryTest {
  @Autowired private UserBlockRepository userBlockRepository;

  @Autowired private UserRepository userRepository;

  private User user;

  private UserBlock userBlock;

  @BeforeEach
  void setUp() {
    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    userRepository.save(user);

    User user2 =
        new User("emily.smith@example.com", "Password123+", "Emily.Smith", "Emily", "Smith");
    userRepository.save(user2);

    userBlock = new UserBlock(user, user2);
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
