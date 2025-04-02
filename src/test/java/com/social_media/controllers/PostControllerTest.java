package com.social_media.controllers;

import com.social_media.converters.CommentConverter;
import com.social_media.converters.PostConverter;
import com.social_media.entities.Comment;
import com.social_media.entities.Like;
import com.social_media.entities.Post;
import com.social_media.entities.User;
import com.social_media.models.CommentDto;
import com.social_media.models.LikeDto;
import com.social_media.models.PageDto;
import com.social_media.models.PostDto;
import com.social_media.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Mock
    private CommentConverter commentConverter;

    private User user;

    private Post post;

    private PostDto postDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("someone@example.com");
        user.setPassword("Password123+");
        user.setUsername("johnDoe");
        user.setName("John");
        user.setLastname("Doe");

        post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setPostedTime(LocalDateTime.now());
        post.setTitle("some title");
        post.setUser(user);
        post.setLikes(new ArrayList<>());

        postDto = new PostDto(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getTitle(),
                (long) post.getLikes().size(),
                post.getPostedTime().toString()
        );
    }

    @Test
    void getPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(post);

        Page<Post> page = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());
        PageDto<Post, PostDto> pageDto = new PageDto<>(page, postConverter);

        when(postService.getFriendsPosts(any(User.class), any(Pageable.class))).thenReturn(pageDto);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<PageDto<Post, PostDto>> response = postController.getPosts(0, 10, authentication);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(posts.size(), response.getBody().getContent().size());
    }

    @Test
    void getUserPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(post);

        Page<Post> page = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());
        PageDto<Post, PostDto> pageDto = new PageDto<>(page, postConverter);

        when(postService.getUserPosts(anyString(), any(Pageable.class), any(User.class))).thenReturn(pageDto);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<PageDto<Post, PostDto>> response = postController.getUserPosts(authentication, user.getId(), 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(posts.size(), response.getBody().getContent().size());
    }

    @Test
    void createPost() {
        when(postConverter.convertToModel(post)).thenReturn(postDto);
        when(postService.createPost(post, user)).thenReturn(post);
        when(postConverter.convertToEntity(postDto)).thenReturn(post);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<PostDto> response = postController.createPost(postDto, authentication);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(post.getId(), response.getBody().id());

    }

    @Test
    void updatePost() {
        when(postConverter.convertToModel(post)).thenReturn(postDto);
        when(postService.updatePost(postDto.id(), postDto.title(), user)).thenReturn(post);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<PostDto> response = postController.updatePost(postDto, authentication);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(post.getId(), response.getBody().id());
    }

    @Test
    void deletePost() {
        doNothing().when(postService).deletePost(any(User.class), anyString());
        when(authentication.getPrincipal()).thenReturn(user);

        postController.deletePost(authentication, post.getId());

        verify(postService, times(1)).deletePost(user, post.getId());
    }

    @Test
    void getPostById() {
        when(postConverter.convertToModel(post)).thenReturn(postDto);
        when(postService.getPostById(post.getId(), user)).thenReturn(post);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<PostDto> response = postController.getPostById(post.getId(), authentication);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(post.getId(), response.getBody().id());
    }

    @Test
    void likePost() {
        Like like = new Like(UUID.randomUUID().toString(), user, post);
        when(postService.likePost(post.getId(), user)).thenReturn(like);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<LikeDto> response = postController.likePost(post.getId(), authentication);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(like.getId(), response.getBody().id());
    }

    @Test
    void removeLike() {
        doNothing().when(postService).removeLike(post.getId(), user);
        when(authentication.getPrincipal()).thenReturn(user);

        postController.removeLike(post.getId(), authentication);

        verify(postService, times(1)).removeLike(post.getId(), user);
    }

    @Test
    void getUserLikedPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(post);

        Page<Post> page = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());
        PageDto<Post, PostDto> pageDto = new PageDto<>(page, postConverter);

        when(postService.getUserLikedPosts(any(User.class), any(Pageable.class))).thenReturn(pageDto);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<PageDto<Post, PostDto>> response = postController.getUserLikedPosts(authentication, 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(posts.size(), response.getBody().getContent().size());
    }

    @Test
    void getComments() {
        Comment comment = new Comment(
                UUID.randomUUID().toString(),
                "some comment",
                LocalDateTime.now(),
                post,
                user
        );

        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        Page<Comment> page = new PageImpl<>(comments, PageRequest.of(0, 10), comments.size());
        PageDto<Comment, CommentDto> pageDto = new PageDto<>(page, commentConverter);

        when(postService.getComments(any(User.class), anyString(), any(Pageable.class))).thenReturn(pageDto);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<PageDto<Comment, CommentDto>> response = postController.getComments(authentication, post.getId(), 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments.size(), response.getBody().getContent().size());
    }
}