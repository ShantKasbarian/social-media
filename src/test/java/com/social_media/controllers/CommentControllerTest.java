package com.social_media.controllers;

import com.social_media.converters.CommentConverter;
import com.social_media.entities.Comment;
import com.social_media.entities.Post;
import com.social_media.entities.User;
import com.social_media.models.CommentDto;
import com.social_media.services.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
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

        comment = new Comment(
                UUID.randomUUID().toString(),
                "some comment",
                LocalDateTime.now(),
                post,
                user
        );

        commentDto = new CommentDto(
                comment.getId(),
                comment.getPost().getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getCommentedTime().toString()
        );
    }

    @Test
    void comment() {
        when(commentConverter.convertToModel(any(Comment.class))).thenReturn(commentDto);
        when(commentService.comment(anyString(), anyString(), any(User.class))).thenReturn(comment);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<CommentDto> response = commentController.comment(commentDto, authentication);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(commentDto.comment(), response.getBody().comment());
    }

    @Test
    void deleteComment() {
        doNothing().when(commentService).deleteComment(commentDto.id(), user);
        when(authentication.getPrincipal()).thenReturn(user);

        commentController.deleteComment(comment.getId(), authentication);

        verify(commentService, times(1)).deleteComment(commentDto.id(), user);
    }

    @Test
    void updateComment() {
        when(commentConverter.convertToModel(any(Comment.class))).thenReturn(commentDto);
        when(commentService.editComment(anyString(), anyString(), any(User.class)))
                .thenReturn(comment);
        when(authentication.getPrincipal()).thenReturn(user);

        ResponseEntity<CommentDto> response = commentController.updateComment(commentDto, authentication);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(comment.getContent(), response.getBody().comment());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}