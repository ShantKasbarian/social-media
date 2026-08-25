package com.social_media.converter;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.PostDto;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
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

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("someone@example.com");
    user.setPassword("Password123+");
    user.setUsername("johnDoe");
    user.setFirstname("John");
    user.setLastname("Doe");

    post = new Post();
    post.setId(UUID.randomUUID());
    post.setTime(Instant.now());
    post.setText("some text");
    post.setUser(user);
    post.setLikes(new ArrayList<>());
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
