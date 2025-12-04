package com.social_media.service.impl;

import com.social_media.entity.*;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceImplTest {
    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    private User user;

    private Post post;

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
    }

    @Test
    void createPost() {
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post response = postService.createPost(user, post);

        assertNotNull(response);
        assertEquals(post, response);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void getPostById() {
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));

        Post response = postService.getPostById(post.getId());

        assertEquals(post, response);
        verify(postRepository).findById(any(UUID.class));
    }

    @Test
    void getPostByIdShouldThrowResourceNotFoundExceptionWhenPostNotFound() {
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(post.getId()));
    }

    @Test
    void updatePost() {
        String text = post.getText();
        String targetText = "some text 2";

        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);


        Post response = postService.updatePost(user, post.getId(), targetText);

        assertEquals(post.getId(), response.getId());
        assertNotEquals(text, response.getText());
        assertEquals(targetText, response.getText());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void updatePostShouldThrowResourceNotFoundExceptionWhenPostNotFound() {
        when(postRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(user, post.getId(), "some text 2"));
    }

    @Test
    void updatePostShouldThrowRequestNotAllowedExceptionWhenUserCallsUpdatePostOfDifferentUser() {
        User user2 = new User();
        user2.setId(UUID.randomUUID());

        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));

        assertThrows(RequestNotAllowedException.class, () -> postService.updatePost(user2, post.getId(), "some text 2"));

    }

    @Test
    void deletePost() {
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
        doNothing().when(postRepository).delete(any(Post.class));

        postService.deletePost(user, post.getId());

        verify(postRepository).findById(any(UUID.class));
        verify(postRepository).delete(any(Post.class));
    }

    @Test
    void deletePostShouldThrowResourceNotFoundExceptionWhenPostNotFound() {
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(user, post.getId()));
    }

    @Test
    void deletePostShouldThrowRequestNotAllowedException() {
        User user2 = new User();
        user2.setId(UUID.randomUUID());

        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(post));
        assertThrows(RequestNotAllowedException.class, () -> postService.deletePost(user2, post.getId()));
    }

    @Test
    void getPostsByUserIdAcceptedFriendRequests() {
        List<Post> posts = new ArrayList<>();
        posts.add(post);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> page = new PageImpl<>(
                posts, pageable, posts.size()
        );

        when(postRepository.findByUserIdAcceptedFriendRequests(any(UUID.class), any(Pageable.class)))
                .thenReturn(page);

        var response = postService.getPostsByUserIdAcceptedFriendRequests(user.getId(), pageable);

        assertNotNull(page);
        assertFalse(response.isEmpty());
        assertEquals(page, response);
        verify(postRepository).findByUserIdAcceptedFriendRequests(any(UUID.class), any(Pageable.class));
    }

    @Test
    void getUserPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(post);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> page = new PageImpl<>(
                posts, pageable, posts.size()
        );

        when(postRepository.findByUserId(any(UUID.class), any(Pageable.class)))
                .thenReturn(page);

        var response = postService.getUserPosts(user, user.getId(), pageable);

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(page, response);
        verify(postRepository).findByUserId(any(UUID.class), any(Pageable.class));
    }

    @Test
    void getUserLikedPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(post);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

        when(postRepository.findByUserIdLikes(any(UUID.class), any(Pageable.class)))
                .thenReturn(page);

        var response = postService.getUserLikedPosts(user.getId(), pageable);

        assertNotNull(response);
        assertEquals(page, response);
        verify(postRepository).findByUserIdLikes(any(UUID.class), any(Pageable.class));
    }
}
