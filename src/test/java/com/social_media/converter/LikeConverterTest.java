package com.social_media.converter;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.LikeDto;
import java.time.Instant;
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

    User user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");

    Post post = new Post("some text", Instant.now(), user);

    like = new Like(user, post);
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
