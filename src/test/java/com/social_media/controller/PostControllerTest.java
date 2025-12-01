package com.social_media.controller;

import com.social_media.converter.PostConverter;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.PostDto;
import com.social_media.service.PostService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PostControllerTest {
    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private PostConverter postConverter;

    @Mock
    private Authentication authentication;

    private User user;

    private Post post;

    private PostDto postDto;

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
        post.setTime(LocalDateTime.now());
        post.setText("some text");
        post.setUser(user);
        post.setLikes(new ArrayList<>());

        postDto = new PostDto(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getText(),
                (long) post.getLikes().size(),
                post.getTime()
        );
    }

    @Test
    void createPost() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(postConverter.convertToModel(any(Post.class))).thenReturn(postDto);
        when(postService.createPost(any(User.class), any(Post.class))).thenReturn(post);
        when(postConverter.convertToEntity(any(PostDto.class))).thenReturn(post);

        var response = postController.createPost(authentication, postDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(postDto, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(postConverter).convertToModel(any(Post.class));
        verify(postService).createPost(any(User.class), any(Post.class));
        verify(postConverter).convertToEntity(any(PostDto.class));
    }

    @Test
    void getPostById() {
        when(postConverter.convertToModel(any(Post.class))).thenReturn(postDto);
        when(postService.getPostById(any(UUID.class))).thenReturn(post);

        var response = postController.getPostById(post.getId());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(postDto, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(postConverter).convertToModel(any(Post.class));
        verify(postService).getPostById(any(UUID.class));
    }

    @Test
    void updatePost() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(postConverter.convertToModel(any(Post.class))).thenReturn(postDto);
        when(postService.updatePost(any(User.class), any(UUID.class), anyString()))
                .thenReturn(post);

        var response = postController.updatePost(authentication, postDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(postDto, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(postConverter).convertToModel(any(Post.class));
        verify(postService).updatePost(any(User.class), any(UUID.class), anyString());
    }

    @Test
    void deletePost() {
        when(authentication.getPrincipal()).thenReturn(user);
        doNothing().when(postService).deletePost(any(User.class), any(UUID.class));

        postController.deletePost(authentication, post.getId());

        verify(postService).deletePost(any(User.class), any(UUID.class));
    }

    @Test
    void getPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(post);

        Page<Post> page = new PageImpl<>(posts);

        when(authentication.getPrincipal()).thenReturn(user);
        when(postService.getFriendsPosts(any(User.class), any(Pageable.class))).thenReturn(page);

        var response = postController.getPosts(authentication, 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(posts.size(), response.getBody().getContent().size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(postService).getFriendsPosts(any(User.class), any(Pageable.class));
    }

    @Test
    void getUserPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(post);

        Page<Post> page = new PageImpl<>(posts);

        when(authentication.getPrincipal()).thenReturn(user);
        when(postService.getUserPosts(any(User.class), any(UUID.class), any(Pageable.class)))
                .thenReturn(page);

        var response = postController.getUserPosts(authentication, user.getId(), 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(posts.size(), response.getBody().getContent().size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(postService).getUserPosts(any(User.class), any(UUID.class), any(Pageable.class));
    }

    @Test
    void getUserLikedPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(post);

        Page<Post> page = new PageImpl<>(posts);

        when(authentication.getPrincipal()).thenReturn(user);
        when(postService.getUserLikedPosts(any(User.class), any(Pageable.class))).thenReturn(page);

        var response = postController.getUserLikedPosts(authentication, 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(posts.size(), response.getBody().getContent().size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(postService).getUserLikedPosts(any(User.class), any(Pageable.class));
    }
}
