package com.social_media.service.impl;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.LikeRepository;
import com.social_media.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LikeServiceImplTest {
    @InjectMocks
    private LikeServiceImpl likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    private User user;

    private Post post;

    private Like like;

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
        post.setTime(LocalDateTime.now());
        post.setText("some text");

        like = new Like(UUID.randomUUID(), user, post);
    }

    @Test
    void createLike() {
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
        when(likeRepository.existsByPostAndUser(any(Post.class), any(User.class))).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        var response = likeService.createLike(user, post.getId());

        assertNotNull(response);
        verify(postRepository).findById(any(UUID.class));
        verify(likeRepository).existsByPostAndUser(any(Post.class), any(User.class));
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void createLikeShouldThrowResourceNotFoundExceptionWhenPostNotFound() {
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> likeService.createLike(user, post.getId()));
    }

    @Test
    void createLikeShouldThrowResourceAlreadyExistsExceptionWhenUserHasLikedPostOnce() {
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
        when(friendRequestRepository.existsByUserIdTargetUserIdStatus(any(UUID.class), any(UUID.class), any(FriendRequest.Status.class)))
                .thenReturn(false);
        when(likeRepository.existsByPostAndUser(any(Post.class), any(User.class)))
                .thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> likeService.createLike(user, post.getId()));
    }

    @Test
    void deleteLikeByPostId() {
        when(likeRepository.findByUserIdPostId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.ofNullable(like));
        doNothing().when(likeRepository).delete(any(Like.class));

        likeService.deleteLikeByPostId(user.getId(), post.getId());

        verify(likeRepository).findByUserIdPostId(any(UUID.class), any(UUID.class));
        verify(likeRepository).delete(any(Like.class));
    }

    @Test
    void deleteLikeShouldThrowResourceNotFoundExceptionWhenLikeByPostIdNotFound() {
        when(likeRepository.findByUserIdPostId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> likeService.deleteLikeByPostId(user.getId(), post.getId()));
    }
}
