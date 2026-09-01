package com.social_media.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class FriendRequestRepositoryTest {
  @Autowired private FriendRequestRepository friendRequestRepository;

  @Autowired private UserRepository userRepository;

  private User user1;

  private User user2;

  private FriendRequest friendRequest;

  @BeforeEach
  void setUp() {
    user1 = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    userRepository.save(user1);

    user2 = new User("emily.smith@example.com", "Password123+", "Emily.Smith", "Emily", "Smith");
    userRepository.save(user2);

    friendRequest = new FriendRequest(user1, user2, FriendRequest.Status.PENDING);
    friendRequestRepository.save(friendRequest);
  }

  @Test
  void existsByUserIdTargetUserId() {
    assertTrue(friendRequestRepository.existsByUserIdTargetUserId(user1.getId(), user2.getId()));
  }

  @Test
  void findByUserStatus() {
    Page<FriendRequest> response =
        friendRequestRepository.findByUserStatus(
            user2, friendRequest.getStatus(), PageRequest.of(0, 10));

    assertNotNull(response);
    assertEquals(friendRequest.getId(), response.getContent().getFirst().getId());
    assertEquals(1, response.getContent().size());
  }
}
