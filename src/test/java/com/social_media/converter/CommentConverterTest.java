package com.social_media.converter;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.Comment;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class CommentConverterTest {
  @InjectMocks private CommentConverter commentConverter;

  private Comment comment;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    User user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");

    Post post = new Post("some text", Instant.now(), user);

    comment = new Comment("some text", Instant.now(), post, user);
  }

  @Test
  void convertToModel() {
    CommentDto commentDto = commentConverter.convertToModel(comment);

    assertNotNull(commentDto);
    assertEquals(comment.getId(), commentDto.id());
    assertEquals(comment.getPost().getId(), commentDto.postId());
    assertEquals(comment.getText(), commentDto.text());
    assertEquals(comment.getUser().getId(), commentDto.userId());
    assertEquals(comment.getUser().getUsername(), commentDto.username());
    assertEquals(comment.getTime(), commentDto.commentedTime());
  }
}
