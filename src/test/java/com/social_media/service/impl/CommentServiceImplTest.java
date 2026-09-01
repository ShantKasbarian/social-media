package com.social_media.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.social_media.entity.*;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.model.CommentDto;
import com.social_media.model.PatchCommentDto;
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

  private PatchCommentDto patchCommentDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    user.setId(UUID.randomUUID());

    user2 = new User("emily.smith@example.com", "Password123+", "Emily.Smith", "Emily", "Smith");
    user2.setId(UUID.randomUUID());

    post = new Post("some text", Instant.now(), user);
    post.setId(UUID.randomUUID());

    FriendRequest friendRequest = new FriendRequest(user, user2, FriendRequest.Status.PENDING);
    friendRequest.setId(UUID.randomUUID());

    comment = new Comment("some text", Instant.now(), post, user);
    comment.setId(UUID.randomUUID());

    commentDto =
        new CommentDto(
            comment.getId(),
            comment.getPost().getId(),
            comment.getText(),
            comment.getUser().getId(),
            comment.getUser().getUsername(),
            comment.getTime());

    patchCommentDto = new PatchCommentDto(comment.getId(), comment.getText());
  }

  @Test
  void create() {
    when(postRepository.findAuthorIdById(any(UUID.class)))
        .thenReturn(Optional.of(post.getUser().getId()));
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class)))
        .thenReturn(false);
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    var response = commentService.create(user2, commentDto);

    assertEquals(comment.getText(), response.getText());
    verify(postRepository).findAuthorIdById(any(UUID.class));
    verify(userBlockRepository).existsBlockBetween(any(UUID.class), any(UUID.class));
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  void update() {
    String oldCommentText = "updated comment";
    comment.setText(oldCommentText);

    when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(comment));

    Comment response = commentService.update(user, patchCommentDto);

    assertNotNull(response);
    assertNotEquals(oldCommentText, response.getText());
    assertEquals(patchCommentDto.text(), response.getText());
    verify(commentRepository).findById(any(UUID.class));
  }

  @Test
  void
      updateShouldThrowRequestNotAllowedExceptionWhenCurrentUserIdIsDifferentFromCommentAuthorId() {
    when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(comment));

    assertThrows(
        RequestNotAllowedException.class, () -> commentService.update(user2, patchCommentDto));
  }

  @Test
  void delete() {
    when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(comment));
    doNothing().when(commentRepository).delete(any(Comment.class));

    commentService.delete(user, comment.getId());

    verify(commentRepository).findById(any(UUID.class));
    verify(commentRepository).delete(any(Comment.class));
  }

  @Test
  void deleteShouldThrowResourceNotFoundExceptionWhenCommentIsNotFound() {
    when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> commentService.delete(user, comment.getId()));
  }

  @Test
  void
      deleteShouldThrowRequestNotAllowedExceptionWhenCurrentUserIdIsDifferentFromCommentAuthorId() {
    when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(comment));
    assertThrows(
        RequestNotAllowedException.class, () -> commentService.delete(user2, comment.getId()));
  }

  @Test
  void findByPostId() {
    Page<Comment> page = new PageImpl<>(List.of(comment));

    when(postRepository.findAuthorIdById(any(UUID.class)))
        .thenReturn(Optional.of(post.getUser().getId()));
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class)))
        .thenReturn(false);
    when(commentRepository.findByPostId(any(UUID.class), any(Pageable.class))).thenReturn(page);

    var response =
        commentService.findByPostId(post.getId(), UUID.randomUUID(), PageRequest.of(0, 10));

    assertNotNull(response);
    assertEquals(page, response);
    verify(commentRepository).findByPostId(any(UUID.class), any(Pageable.class));
  }
}
