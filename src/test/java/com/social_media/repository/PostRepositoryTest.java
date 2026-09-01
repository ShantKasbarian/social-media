package com.social_media.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.*;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class PostRepositoryTest {
  @Autowired private PostRepository postRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private FriendRequestRepository friendRequestRepository;

  @Autowired private LikeRepository likeRepository;

  private User user;

  private Post post;

  @BeforeEach
  void setUp() {
    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    userRepository.save(user);

    User user2 =
        new User("emily.smith@example.com", "Password123+", "Emily.Smith", "Emily", "Smith");
    userRepository.save(user2);

    post = new Post("some text", Instant.now(), user2);
    postRepository.save(post);

    Like like = new Like(user, post);
    likeRepository.save(like);

    FriendRequest friendRequest = new FriendRequest(user, user2, FriendRequest.Status.ACCEPTED);
    friendRequestRepository.save(friendRequest);
  }

  @Test
  void findByUserId() {
    Page<Post> response =
        postRepository.findByUserId(post.getUser().getId(), PageRequest.of(0, 10));

    assertNotNull(response);
    assertFalse(response.getContent().isEmpty());
    assertEquals(post.getId(), response.getContent().getFirst().getId());
    assertNotEquals(user.getId(), response.getContent().getFirst().getUser().getId());
  }

  @Test
  void findByUserIdAcceptedFriendRequests() {
    Page<Post> response =
        postRepository.findByUserIdAcceptedFriendRequests(user.getId(), PageRequest.of(0, 10));

    assertNotNull(response);
    assertFalse(response.getContent().isEmpty());
    assertEquals(post.getId(), response.getContent().getFirst().getId());
    assertNotEquals(user.getId(), response.getContent().getFirst().getUser().getId());
  }

  @Test
  void findByUserIdLikes() {
    Page<Post> response = postRepository.findByUserIdLikes(user.getId(), PageRequest.of(0, 10));

    assertNotNull(response);
    assertFalse(response.getContent().isEmpty());
    assertEquals(post.getId(), response.getContent().getFirst().getId());
  }
}
