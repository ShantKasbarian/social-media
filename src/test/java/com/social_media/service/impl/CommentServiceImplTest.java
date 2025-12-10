package com.social_media.service.impl;

import com.social_media.entity.*;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {
    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    private User user;

    private Comment comment;

    private Post post;

    private FriendRequest friendRequest;

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

        friendRequest = new FriendRequest();
        friendRequest.setId(UUID.randomUUID());
        friendRequest.setUser(user);
        friendRequest.setTargetUser(new User());
        friendRequest.setStatus(FriendRequest.Status.PENDING);

        comment = new Comment(UUID.randomUUID(), "some text", LocalDateTime.now(), post, user);
    }

    @Test
    void createComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        var response = commentService.createComment(user, comment);

        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getText(), response.getText());
        assertEquals(comment.getUser().getId(), response.getUser().getId());
        assertEquals(comment.getPost().getId(), response.getPost().getId());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void updateComment() {
        String oldCommentText = comment.getText();
        String targetCommentText = "updated comment";

        when(commentRepository.findById(any(UUID.class)))
                .thenReturn(Optional.ofNullable(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment response = commentService.updateComment(user, comment.getId(), targetCommentText);

        assertNotEquals(oldCommentText, response.getText());
        assertEquals(targetCommentText, response.getText());
        verify(commentRepository).findById(any(UUID.class));
        verify(commentRepository).save(comment);
    }

    @Test
    void updateCommentShouldThrowRequestNotAllowedExceptionWhenCurrentUserIdIsDifferentFromCommentAuthorId() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(commentRepository.findById(any(UUID.class)))
                .thenReturn(Optional.ofNullable(comment));

        assertThrows(RequestNotAllowedException.class, () -> commentService.updateComment(user, comment.getId(), comment.getText()));
    }

    @Test
    void deleteComment() {
        when(commentRepository.findById(any(UUID.class)))
                .thenReturn(Optional.ofNullable(comment));
        doNothing().when(commentRepository).delete(any(Comment.class));

        commentService.deleteComment(user, comment.getId());

        verify(commentRepository).findById(any(UUID.class));
        verify(commentRepository).delete(any(Comment.class));
    }

    @Test
    void deleteCommentShouldThrowResourceNotFoundException() {
        when(commentRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(user, comment.getId()));
    }

    @Test
    void deleteCommentShouldThrowRequestNotAllowedException() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(comment));
        assertThrows(RequestNotAllowedException.class, () -> commentService.deleteComment(user, comment.getId()));
    }

    @Test
    void getCommentsByPostId() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        Page<Comment> page = new PageImpl<>(comments);

        when(commentRepository.findByPostId(any(UUID.class), any(Pageable.class)))
                .thenReturn(page);

        var response = commentService.getCommentsByPostId(post.getId(), PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(page, response);
        verify(commentRepository).findByPostId(any(UUID.class), any(Pageable.class));
    }
}
