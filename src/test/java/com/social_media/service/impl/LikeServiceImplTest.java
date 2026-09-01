package com.social_media.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.LikeRepository;
import com.social_media.repository.PostRepository;
import com.social_media.repository.UserBlockRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LikeServiceImplTest {
  @InjectMocks private LikeServiceImpl likeService;

  @Mock private LikeRepository likeRepository;

  @Mock private UserBlockRepository userBlockRepository;

  @Mock private PostRepository postRepository;

  private User user;

  private Post post;

  private Like like;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    user.setId(UUID.randomUUID());

    post = new Post("some text", Instant.now(), user);
    post.setId(UUID.randomUUID());

    like = new Like(user, post);
    like.setId(UUID.randomUUID());
  }

  @Test
  void create() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class)))
        .thenReturn(false);
    when(likeRepository.existsByPostAndUser(any(Post.class), any(User.class))).thenReturn(false);
    when(likeRepository.save(any(Like.class))).thenReturn(like);

    var response = likeService.create(user, post.getId());

    assertNotNull(response);
    verify(postRepository).findById(any(UUID.class));
    verify(likeRepository).existsByPostAndUser(any(Post.class), any(User.class));
    verify(likeRepository).save(any(Like.class));
  }

  @Test
  void createShouldThrowResourceNotFoundExceptionWhenPostIsNotFound() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> likeService.create(user, post.getId()));
  }

  @Test
  void createShouldThrowRequestNotAllowedExceptionWhenUserBlockExistsBetweenAuthorAndUser() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class))).thenReturn(true);

    assertThrows(RequestNotAllowedException.class, () -> likeService.create(user, post.getId()));
  }

  @Test
  void createShouldThrowResourceAlreadyExistsExceptionWhenLikeByCurrentUserAlreadyExists() {
    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
    when(userBlockRepository.existsBlockBetween(any(UUID.class), any(UUID.class)))
        .thenReturn(false);
    when(likeRepository.existsByPostAndUser(any(Post.class), any(User.class))).thenReturn(true);
    assertThrows(
        ResourceAlreadyExistsException.class, () -> likeService.create(user, post.getId()));
  }

  @Test
  void delete() {
    when(likeRepository.findByUserIdPostId(any(UUID.class), any(UUID.class)))
        .thenReturn(Optional.ofNullable(like));
    doNothing().when(likeRepository).delete(any(Like.class));

    likeService.delete(user, post.getId());

    verify(likeRepository).findByUserIdPostId(any(UUID.class), any(UUID.class));
    verify(likeRepository).delete(any(Like.class));
  }

  @Test
  void deleteShouldThrowResourceNotFoundExceptionWhenLikeByPostIdIsNotFound() {
    when(likeRepository.findByUserIdPostId(any(UUID.class), any(UUID.class)))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> likeService.delete(user, post.getId()));
  }
}
