package com.social_media.controller;

import com.social_media.converter.CommentConverter;
import com.social_media.entity.Comment;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.model.PageDto;
import com.social_media.service.CommentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {
  static final String TIME_PROPERTY = "time";

  private final CommentService commentService;

  private final CommentConverter commentConverter;

  @PostMapping
  public ResponseEntity<CommentDto> createComment(
      Authentication authentication, @RequestBody @Valid CommentDto commentDto) {
    log.info("/comments with POST called, creating comment");

    User user = (User) authentication.getPrincipal();
    Comment comment = commentConverter.convertToEntity(commentDto);
    comment.setUser(user);

    var result = commentConverter.convertToModel(commentService.createComment(comment));

    log.info("created comment");

    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  @PutMapping
  public ResponseEntity<CommentDto> updateComment(
      Authentication authentication, @RequestBody @Valid CommentDto commentDto) {
    UUID id = commentDto.id();

    log.info("/comments with PUT called, updating comment with id {}", id);

    User user = (User) authentication.getPrincipal();

    var comment =
        commentConverter.convertToModel(commentService.updateComment(user, id, commentDto.text()));

    log.info("updated comment with id {}", id);

    return ResponseEntity.ok(comment);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteComment(
      Authentication authentication, @PathVariable UUID id) {
    log.info("/comments/{} with DELETE called, deleting comment with the specified id", id);

    User user = (User) authentication.getPrincipal();

    commentService.deleteComment(user, id);

    log.info("deleted comment with id {}", id);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/posts/{postId}")
  public ResponseEntity<PageDto<Comment, CommentDto>> getCommentsByPostId(
      @AuthenticationPrincipal Authentication authentication,
      @PathVariable UUID postId,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size) {
    log.info(
        "/comments/posts/{} with GET called, fetching comments with the specified postId", postId);

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(TIME_PROPERTY)));
    User user = (User) authentication.getPrincipal();

    var comments =
        new PageDto<>(
            commentService.getCommentsByPostId(postId, user.getId(), pageable), commentConverter);

    log.info("fetched comments with postId {}", postId);

    return ResponseEntity.ok(comments);
  }
}
