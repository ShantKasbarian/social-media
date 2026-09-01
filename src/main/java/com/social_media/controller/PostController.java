package com.social_media.controller;

import static com.social_media.controller.CommentController.TIME_PROPERTY;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.mapper.PostMapper;
import com.social_media.model.PageDto;
import com.social_media.model.PostDto;
import com.social_media.service.PostService;
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
@RequestMapping("/posts")
public class PostController {
  private final PostService postService;

  private final PostMapper postMapper;

  @PostMapping
  public ResponseEntity<PostDto> createPost(
      Authentication authentication, @RequestBody @Valid PostDto postDto) {
    User user = (User) authentication.getPrincipal();

    Post post = postService.create(user, postDto);
    PostDto responseDto = postMapper.toModel(post);

    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PostDto> getPostById(Authentication authentication, @PathVariable UUID id) {
    User user = (User) authentication.getPrincipal();

    var post = postService.findById(id, user);
    var postDto = postMapper.toModel(post);

    return ResponseEntity.ok(postDto);
  }

  @PutMapping
  public ResponseEntity<PostDto> updatePost(
      Authentication authentication, @RequestBody @Valid PostDto postDto) {
    User user = (User) authentication.getPrincipal();

    var post = postService.update(user, postDto);
    var responseDto = postMapper.toModel(post);

    return ResponseEntity.ok(responseDto);
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(Authentication authentication, @PathVariable UUID postId) {
    User user = (User) authentication.getPrincipal();

    postService.delete(user, postId);

    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<PageDto<Post, PostDto>> getPostsByUserIdAcceptedFriendRequests(
      Authentication authentication,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size) {
    User user = (User) authentication.getPrincipal();
    Pageable pageable = PageRequest.of(page, size);

    var posts = postService.findByUserIdAcceptedFriendRequests(user.getId(), pageable);
    var pageDto = new PageDto<>(posts, postMapper::toModel);

    return ResponseEntity.ok(pageDto);
  }

  @GetMapping("/users/{userId}")
  public ResponseEntity<PageDto<Post, PostDto>> getUserPosts(
      Authentication authentication,
      @PathVariable UUID userId,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size) {
    User user = (User) authentication.getPrincipal();
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(TIME_PROPERTY)));

    var posts = postService.findByUserId(user, userId, pageable);
    var pageDto = new PageDto<>(posts, postMapper::toModel);

    return ResponseEntity.ok(pageDto);
  }

  @GetMapping("/likes")
  public ResponseEntity<PageDto<Post, PostDto>> getUserLikedPosts(
      Authentication authentication,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size) {
    User user = (User) authentication.getPrincipal();
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(TIME_PROPERTY)));

    var posts = postService.findLikedByUserId(user.getId(), pageable);
    var pageDto = new PageDto<>(posts, postMapper::toModel);

    return ResponseEntity.ok(pageDto);
  }
}
