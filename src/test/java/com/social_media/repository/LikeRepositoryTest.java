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
    user = new User();
    user.setEmail("someone@example.com");
    user.setPassword("Password123+");
    user.setUsername("johnDoe");
    user.setFirstname("John");
    user.setLastname("Doe");

    post = new Post();
    post.setUser(user);
    post.setTime(Instant.now());
    post.setText("some text");

    userRepository.save(user);
    postRepository.save(post);

    Like like = new Like();
    like.setUser(user);
    like.setPost(post);

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
