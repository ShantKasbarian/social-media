package com.social_media.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.social_media.entity.Comment;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.mapper.CommentMapper;
import com.social_media.model.CommentDto;
import com.social_media.model.PatchCommentDto;
import com.social_media.service.CommentService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

class CommentControllerTest {
  @InjectMocks private CommentController commentController;

  @Mock private CommentService commentService;

  @Mock private CommentMapper commentMapper;

  @Mock private Authentication authentication;

  private User user;

  private Comment comment;

  private Post post;

  private CommentDto commentDto;

  private PatchCommentDto patchCommentDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    user.setId(UUID.randomUUID());

    post = new Post("some text", Instant.now(), user);
    post.setId(UUID.randomUUID());

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
  void createComment() {
    when(authentication.getPrincipal()).thenReturn(user);
    when(commentMapper.toModel(any(Comment.class))).thenReturn(commentDto);
    when(commentService.create(any(User.class), any(CommentDto.class))).thenReturn(comment);

    var response = commentController.createComment(authentication, commentDto);

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(commentDto, response.getBody());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    verify(authentication).getPrincipal();
    verify(commentMapper).toModel(any(Comment.class));
    verify(commentService).create(any(User.class), any(CommentDto.class));
  }

  @Test
  void updateComment() {
    when(authentication.getPrincipal()).thenReturn(user);
    when(commentService.update(any(User.class), any(PatchCommentDto.class))).thenReturn(comment);
    when(commentMapper.toModel(any(Comment.class))).thenReturn(commentDto);

    var response = commentController.updateComment(authentication, patchCommentDto);

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(commentDto, response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(authentication).getPrincipal();
    verify(commentMapper).toModel(any(Comment.class));
    verify(commentService).update(any(User.class), any(PatchCommentDto.class));
  }

  @Test
  void deleteComment() {
    when(authentication.getPrincipal()).thenReturn(user);
    doNothing().when(commentService).delete(any(User.class), any(UUID.class));

    var response = commentController.deleteComment(authentication, comment.getId());

    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(commentService).delete(any(User.class), any(UUID.class));
  }

  @Test
  void getCommentsByPostId() {
    List<Comment> comments = new ArrayList<>();
    comments.add(comment);

    Page<Comment> page = new PageImpl<>(comments);

    when(authentication.getPrincipal()).thenReturn(user);
    when(commentService.findByPostId(any(UUID.class), any(UUID.class), any(Pageable.class)))
        .thenReturn(page);

    var response = commentController.getCommentsByPostId(authentication, post.getId(), 0, 10);

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(page.getTotalPages(), response.getBody().getTotalPages());
    assertEquals(page.getTotalElements(), response.getBody().getTotalElements());
    verify(commentService).findByPostId(any(UUID.class), any(UUID.class), any(Pageable.class));
  }
}
