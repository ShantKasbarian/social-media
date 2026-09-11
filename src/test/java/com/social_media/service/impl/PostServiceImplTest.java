package com.social_media.service.impl;

import static com.social_media.service.impl.LikeServiceImpl.BLOCKED_USER_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.social_media.entity.*;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.model.PostDto;
import com.social_media.repository.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

class PostServiceImplTest {
  public static final String POST_NOT_FOUND_MESSAGE = "post not found";

  private static final String UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE =
      "unable to delete or modify post";

  @InjectMocks private PostServiceImpl postService;

  @Mock private PostRepository postRepository;

  @Mock private UserBlockRepository userBlockRepository;

  private User user;

  private User user2;

  private Post post;

  private PostDto postDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    user.setId(UUID.randomUUID());

    user2 = new User("emily.smith@example.com", "Password123+", "Emily.Smith", "Emily", "Smith");
    user2.setId(UUID.randomUUID());

    post = new Post("some text", Instant.now(), user);
    post.setId(UUID.randomUUID());
    post.setLikes(List.of());

    postDto =
        new PostDto(
            post.getId(),
            post.getUser().getId(),
            post.getUser().getUsername(),
            post.getText(),
            (long) post.getLikes().size(),
            post.getTime());
  }

  @Test
  void create() {
    when(postRepository.save(any(Post.class))).thenReturn(post);

    Post response = postService.create(user, postDto);

    assertNotNull(response);
    verify(postRepository).save(any(Post.class));
  }

  @Test
  void findById() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));

    Post response = postService.findById(post.getId(), user);

    assertEquals(post, response);
    verify(postRepository).findById(any(UUID.class));
  }

  @Test
  void findByIdShouldThrowResourceNotFoundExceptionWhenPostIsNotFound() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> postService.findById(post.getId(), user));
  }

  @Test
  void
      findByIdShouldThrowRequestNotAllowedExceptionWhenBlockRelationshipExistsBetweenCurrentUserAndAuthor() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class))).thenReturn(true);

    Exception exception =
        assertThrows(
            RequestNotAllowedException.class, () -> postService.findById(post.getId(), user2));
    assertEquals(BLOCKED_USER_MESSAGE, exception.getMessage());
  }

  @Test
  void update() {
    post.setText("some different text");
    String targetText = postDto.text();

    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));

    Post response = postService.update(user, postDto);

    assertNotNull(response);
    assertEquals(targetText, response.getText());

    verify(postRepository).findById(any(UUID.class));
  }

  @Test
  void updateShouldThrowResourceNotFoundExceptionWhenPostIsNotFound() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(ResourceNotFoundException.class, () -> postService.update(user, postDto));
    assertEquals(POST_NOT_FOUND_MESSAGE, exception.getMessage());
  }

  @Test
  void updateShouldThrowRequestNotAllowedExceptionWhenCurrentUserIsNotAuthor() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));

    Exception exception =
        assertThrows(RequestNotAllowedException.class, () -> postService.update(user2, postDto));
    assertEquals(UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE, exception.getMessage());
  }

  @Test
  void delete() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
    doNothing().when(postRepository).delete(any(Post.class));

    postService.delete(user, post.getId());

    verify(postRepository).findById(any(UUID.class));
    verify(postRepository).delete(any(Post.class));
  }

  @Test
  void deleteShouldThrowResourceNotFoundExceptionWhenPostIsNotFound() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(ResourceNotFoundException.class, () -> postService.delete(user, post.getId()));
    assertEquals(POST_NOT_FOUND_MESSAGE, exception.getMessage());
  }

  @Test
  void deletePostShouldThrowRequestNotAllowedExceptionWhenCurrentUserIsNotAuthor() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));

    Exception exception =
        assertThrows(
            RequestNotAllowedException.class, () -> postService.delete(user2, post.getId()));
    assertEquals(UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE, exception.getMessage());
  }

  @Test
  void findByUserIdAcceptedFriendRequests() {
    List<Post> posts = new ArrayList<>();
    posts.add(post);

    Pageable pageable = PageRequest.of(0, 10);

    Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

    when(postRepository.findByUserIdAcceptedFriendRequests(any(UUID.class), any(Pageable.class)))
        .thenReturn(page);

    var response = postService.findByUserIdAcceptedFriendRequests(user.getId(), pageable);

    assertNotNull(page);
    assertFalse(response.isEmpty());
    assertEquals(page, response);
    verify(postRepository).findByUserIdAcceptedFriendRequests(any(UUID.class), any(Pageable.class));
  }

  @Test
  void findByUserId() {
    List<Post> posts = new ArrayList<>();
    posts.add(post);

    Pageable pageable = PageRequest.of(0, 10);

    Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

    when(postRepository.findByUserId(any(UUID.class), any(Pageable.class))).thenReturn(page);

    var response = postService.findByUserId(user, user.getId(), pageable);

    assertNotNull(response);
    assertFalse(response.isEmpty());
    assertEquals(page, response);
    verify(postRepository).findByUserId(any(UUID.class), any(Pageable.class));
  }

  @Test
  void
      findByUserIdShouldThrowRequestNotAllowedExceptionWhenBlockRelationshipExistsBetweenCurrentUserAndTargetUser() {
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class))).thenReturn(true);

    Exception exception =
        assertThrows(
            RequestNotAllowedException.class,
            () -> postService.findByUserId(user, user2.getId(), PageRequest.of(0, 10)));
    assertEquals(BLOCKED_USER_MESSAGE, exception.getMessage());
  }

  @Test
  void findLikedByUserId() {
    List<Post> posts = new ArrayList<>();
    posts.add(post);

    Pageable pageable = PageRequest.of(0, 10);

    Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

    when(postRepository.findByUserIdLikes(any(UUID.class), any(Pageable.class))).thenReturn(page);

    var response = postService.findLikedByUserId(user.getId(), pageable);

    assertNotNull(response);
    assertEquals(page, response);
    verify(postRepository).findByUserIdLikes(any(UUID.class), any(Pageable.class));
  }
}
