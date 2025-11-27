package com.social_media.controller;

import com.social_media.converter.CommentConverter;
import com.social_media.entity.Comment;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.service.CommentService;
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

class CommentControllerTest {
    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @Mock
    private CommentConverter commentConverter;

    @Mock
    private Authentication authentication;

    private User user;

    private Comment comment;

    private Post post;

    private CommentDto commentDto;

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

        comment = new Comment(
                UUID.randomUUID(),
                "some text",
                LocalDateTime.now(),
                post,
                user
        );

        commentDto = new CommentDto(
                comment.getId(),
                comment.getPost().getId(),
                comment.getText(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getTime()
        );
    }

    @Test
    void createComment() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(commentConverter.convertToModel(any(Comment.class))).thenReturn(commentDto);
        when(commentService.createComment(any(User.class), any(Comment.class)))
                .thenReturn(comment);
        when(commentConverter.convertToEntity(any(CommentDto.class))).thenReturn(comment);

        var response = commentController.createComment(authentication, commentDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(commentDto, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(commentConverter).convertToModel(any(Comment.class));
        verify(commentService).createComment(any(User.class), any(Comment.class));
        verify(commentConverter).convertToEntity(any(CommentDto.class));
    }

    @Test
    void updateComment() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(commentConverter.convertToModel(any(Comment.class))).thenReturn(commentDto);
        when(commentService.updateComment(any(User.class), any(UUID.class), anyString()))
                .thenReturn(comment);

        var response = commentController.updateComment(authentication, commentDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(commentDto, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(commentConverter).convertToModel(any(Comment.class));
        verify(commentService).updateComment(any(User.class), any(UUID.class), anyString());
    }

    @Test
    void deleteComment() {
        when(authentication.getPrincipal()).thenReturn(user);
        doNothing().when(commentService).deleteComment(any(User.class), any(UUID.class));

        commentController.deleteComment(authentication, comment.getId());

        verify(commentService).deleteComment(any(User.class), any(UUID.class));
    }

    @Test
    void getCommentsByPostId() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        Page<Comment> page = new PageImpl<>(comments);

        when(authentication.getPrincipal()).thenReturn(user);
        when(commentService.getCommentsByPostId(any(User.class), any(UUID.class), any(Pageable.class)))
                .thenReturn(page);

        var response = commentController.getCommentsByPostId(authentication, post.getId(), 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(page.getTotalPages(), response.getBody().getTotalPages());
        assertEquals(page.getTotalElements(), response.getBody().getTotalElements());
        verify(authentication).getPrincipal();
        verify(commentService).getCommentsByPostId(any(User.class), any(UUID.class), any(Pageable.class));
    }
}
