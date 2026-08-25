package com.social_media.converter;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.LikeDto;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class LikeConverterTest {
  @InjectMocks private LikeConverter likeConverter;

  private Like like;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("someone@example.com");
    user.setPassword("Password123+");
    user.setUsername("johnDoe");
    user.setFirstname("John");
    user.setLastname("Doe");

    Post post = new Post();
    post.setId(UUID.randomUUID());
    post.setUser(user);
    post.setTime(Instant.now());
    post.setText("some text");

    like = new Like(UUID.randomUUID(), user, post);
  }

  @Test
  void convertToModel() {
    LikeDto likeDto = likeConverter.convertToModel(like);

    assertNotNull(likeDto);
    assertEquals(like.getId(), likeDto.id());
    assertEquals(like.getUser().getId(), likeDto.userId());
    assertEquals(like.getUser().getUsername(), likeDto.username());
    assertEquals(like.getPost().getId(), likeDto.postId());
  }
}
