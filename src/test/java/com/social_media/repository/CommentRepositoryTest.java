package com.social_media.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.social_media.entity.Comment;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class CommentRepositoryTest {
  @Autowired private CommentRepository commentRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private PostRepository postRepository;

  private Post post;

  private Comment comment;

  @BeforeEach
  void setUp() {
    User user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    userRepository.save(user);

    post = new Post("some text", Instant.now(), user);
    postRepository.save(post);

    comment = new Comment("some text", Instant.now(), post, user);
    commentRepository.save(comment);
  }

  @Test
  void findByPostId() {
    Page<Comment> comments = commentRepository.findByPostId(post.getId(), PageRequest.of(0, 10));
    Comment response = comments.getContent().getFirst();

    assertEquals(comment.getText(), response.getText());
    assertEquals(comment.getTime(), response.getTime());
    assertEquals(comment.getUser(), response.getUser());
    assertEquals(comment.getPost(), response.getPost());
  }
}
