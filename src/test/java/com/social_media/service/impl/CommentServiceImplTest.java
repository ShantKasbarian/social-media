package com.social_media.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.social_media.entity.*;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.model.CommentDto;
import com.social_media.repository.CommentRepository;
import com.social_media.repository.PostRepository;
import com.social_media.repository.UserBlockRepository;
import com.social_media.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class CommentServiceImplTest {
  @InjectMocks private CommentServiceImpl commentService;

  @Mock private CommentRepository commentRepository;

  @Mock private PostRepository postRepository;

  @Mock private UserRepository userRepository;

  @Mock private UserBlockRepository userBlockRepository;

  private User user;

  private User user2;

  private Comment comment;

  private Post post;

  private CommentDto commentDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("someone@example.com");
    user.setPassword("Password123+");
    user.setUsername("johnDoe");
    user.setFirstname("John");
    user.setLastname("Doe");

    post = new Post();
    post.setId(UUID.randomUUID());
    post.setUser(user);
    post.setTime(Instant.now());
    post.setText("some text");

    FriendRequest friendRequest = new FriendRequest();
    friendRequest.setId(UUID.randomUUID());
    friendRequest.setUser(user);
    friendRequest.setTargetUser(new User());
    friendRequest.setStatus(FriendRequest.Status.PENDING);

    comment = new Comment(UUID.randomUUID(), "some text", Instant.now(), post, user);

    commentDto =
        new CommentDto(
            comment.getId(),
            comment.getPost().getId(),
            comment.getText(),
            comment.getUser().getId(),
            comment.getUser().getUsername(),
            comment.getTime());

    user2 = new User();
    user2.setId(UUID.randomUUID());
  }

  @Test
  void createComment() {
    when(postRepository.findAuthorIdById(any(UUID.class)))
        .thenReturn(Optional.of(post.getUser().getId()));
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class)))
        .thenReturn(false);
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    var response = commentService.createComment(user2, commentDto);

    assertEquals(comment.getText(), response.getText());
    verify(postRepository).findAuthorIdById(any(UUID.class));
    verify(userBlockRepository).existsBlockBetween(any(UUID.class), any(UUID.class));
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  void updateComment() {
    String oldCommentText = "updated comment";
    comment.setText(oldCommentText);

    when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(comment));

    Comment response = commentService.updateComment(user, commentDto);

    assertNotNull(response);
    assertNotEquals(oldCommentText, response.getText());
    assertEquals(commentDto.text(), response.getText());
    verify(commentRepository).findById(any(UUID.class));
  }

  @Test
  void
      updateCommentShouldThrowRequestNotAllowedExceptionWhenCurrentUserIdIsDifferentFromCommentAuthorId() {
    User user = new User();
    user.setId(UUID.randomUUID());

    when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(comment));

    assertThrows(
        RequestNotAllowedException.class, () -> commentService.updateComment(user, commentDto));
  }

  @Test
  void deleteComment() {
    when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(comment));
    doNothing().when(commentRepository).delete(any(Comment.class));

    commentService.deleteComment(user, comment.getId());

    verify(commentRepository).findById(any(UUID.class));
    verify(commentRepository).delete(any(Comment.class));
  }

  @Test
  void deleteCommentShouldThrowResourceNotFoundException() {
    when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> commentService.deleteComment(user, comment.getId()));
  }

  @Test
  void deleteCommentShouldThrowRequestNotAllowedException() {
    User user = new User();
    user.setId(UUID.randomUUID());

    when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(comment));
    assertThrows(
        RequestNotAllowedException.class,
        () -> commentService.deleteComment(user, comment.getId()));
  }

  @Test
  void getCommentsByPostId() {
    Page<Comment> page = new PageImpl<>(List.of(comment));

    when(postRepository.findAuthorIdById(any(UUID.class)))
        .thenReturn(Optional.of(post.getUser().getId()));
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class)))
        .thenReturn(false);
    when(commentRepository.findByPostId(any(UUID.class), any(Pageable.class))).thenReturn(page);

    var response =
        commentService.getCommentsByPostId(post.getId(), UUID.randomUUID(), PageRequest.of(0, 10));

    assertNotNull(response);
    assertEquals(page, response);
    verify(commentRepository).findByPostId(any(UUID.class), any(Pageable.class));
  }
}
