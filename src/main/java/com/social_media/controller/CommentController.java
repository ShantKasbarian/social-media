package com.social_media.controller;

import com.social_media.converter.CommentConverter;
import com.social_media.entity.Comment;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.model.PageDto;
import com.social_media.model.PatchCommentDto;
import com.social_media.service.CommentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    User user = (User) authentication.getPrincipal();

    var comment = commentService.create(user, commentDto);
    var responseDto = commentConverter.convertToModel(comment);

    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @PatchMapping
  public ResponseEntity<CommentDto> updateComment(
      Authentication authentication, @RequestBody @Valid PatchCommentDto patchCommentDto) {
    User user = (User) authentication.getPrincipal();

    var comment = commentService.update(user, patchCommentDto);
    var commentDto = commentConverter.convertToModel(comment);

    return ResponseEntity.ok(commentDto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteComment(Authentication authentication, @PathVariable UUID id) {
    User user = (User) authentication.getPrincipal();

    commentService.delete(user, id);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/posts/{postId}")
  public ResponseEntity<PageDto<Comment, CommentDto>> getCommentsByPostId(
      Authentication authentication,
      @PathVariable UUID postId,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(TIME_PROPERTY)));
    User user = (User) authentication.getPrincipal();

    var comments = commentService.findByPostId(postId, user.getId(), pageable);
    var pageDto = new PageDto<>(comments, commentConverter);

    return ResponseEntity.ok(pageDto);
  }
}
