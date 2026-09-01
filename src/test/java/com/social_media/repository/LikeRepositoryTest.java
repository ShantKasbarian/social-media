package com.social_media.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class LikeRepositoryTest {
  @Autowired private LikeRepository likeRepository;

  @Autowired private PostRepository postRepository;

  @Autowired private UserRepository userRepository;

  private User user;

  private Post post;

  @BeforeEach
  void setUp() {
    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    userRepository.save(user);

    post = new Post("some text", Instant.now(), user);
    postRepository.save(post);

    Like like = new Like(user, post);
    likeRepository.save(like);
  }

  @Test
  void existsByPostAndUser() {
    assertTrue(likeRepository.existsByPostAndUser(post, user));
  }

  @Test
  void findByUserIdPostId() {
    Like like = likeRepository.findByUserIdPostId(user.getId(), post.getId()).get();

    assertNotNull(like);
    assertEquals(user.getId(), like.getUser().getId());
    assertEquals(post.getId(), like.getPost().getId());
  }
}
