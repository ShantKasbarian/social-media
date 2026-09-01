package com.social_media.converter;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.PostDto;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class PostConverterTest {
  @InjectMocks private PostConverter postConverter;

  private Post post;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    User user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");

    post = new Post("some text", Instant.now(), user);
  }

  @Test
  void convertToModel() {
    PostDto postDto = postConverter.convertToModel(post);

    assertNotNull(postDto);
    assertEquals(post.getId(), postDto.id());
    assertEquals(post.getUser().getId(), postDto.userId());
    assertEquals(post.getUser().getUsername(), postDto.username());
    assertEquals(post.getText(), postDto.text());
    assertEquals(post.getTime(), postDto.postedTime());
  }
}
