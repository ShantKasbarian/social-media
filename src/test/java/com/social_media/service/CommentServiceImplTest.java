package com.social_media.service;

import com.social_media.entity.*;
import com.social_media.exception.InvalidProvidedInfoException;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.CommentRepository;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.PostRepository;
import com.social_media.service.impl.CommentServiceImpl;
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

class CommentServiceImplTest {
    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    private User user;

    private Comment comment;

    private Post post;

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
        post.setUser(user);
        post.setPostedTime(LocalDateTime.now());
        post.setTitle("some title");

        comment = new Comment(
                UUID.randomUUID().toString(),
                "some comment",
                LocalDateTime.now(),
                post,
                user
        );
    }

    @Test
    void comment() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(friendRequestRepository.findByUser_idFriend_id(anyString(), anyString())).thenReturn(Optional.empty());

        Comment response = commentService.comment(comment.getContent(), post.getId(), user);

        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getContent(), response.getContent());
        assertEquals(comment.getUser().getId(), response.getUser().getId());
        assertEquals(comment.getPost().getId(), response.getPost().getId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void commentShouldThrowInvalidProvidedInfoExceptionWhenContentIsNull() {
        assertThrows(InvalidProvidedInfoException.class, () -> commentService.comment(null, post.getId(), user));
    }

    @Test
    void commentShouldThrowInvalidProvidedInfoExceptionWhenContentIsEmpty() {
        assertThrows(InvalidProvidedInfoException.class, () -> commentService.comment("", post.getId(), user));
    }

    @Test
    void commentShouldThrowResourceNotFoundExceptionWhenPostIsNotFound() {
        when(postRepository.findById(anyString())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> commentService.comment(comment.getContent(), post.getId(), user));
    }

    @Test
    void commentShouldThrowRequestNotAllowedExceptionWhenUserIsBlocked() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setStatus(FriendshipStatus.BLOCKED);

        when(postRepository.findById(anyString())).thenReturn(Optional.ofNullable(post));
        when(friendRequestRepository.findByUser_idFriend_id(anyString(), anyString()))
                .thenReturn(Optional.of(friendRequest));
        assertThrows(RequestNotAllowedException.class, () -> commentService.comment(comment.getContent(), post.getId(), user));
    }

    @Test
    void editComment() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        String oldComment = comment.getContent();
        Comment response = commentService.editComment(comment.getId(), "some edited comment", user);

        assertNotEquals(oldComment, response.getContent());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void editCommentShouldThrowInvalidProvidedInfoExceptionWhenContentIsNull() {
        assertThrows(InvalidProvidedInfoException.class, () -> commentService.editComment(comment.getId(), null, user));
    }

    @Test
    void editCommentShouldThrowInvalidProvidedInfoExceptionWhenContentIsEmpty() {
        assertThrows(InvalidProvidedInfoException.class, () -> commentService.editComment(comment.getId(), "", user));
    }

    @Test
    void editCommentShouldThrowResourceNotFoundExceptionWhenCommentIsNotFound() {
        when(commentRepository.findById(comment.getId())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> commentService.editComment(comment.getId(), "some edited comment", user));
    }

    @Test
    void deleteComment() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        doNothing().when(commentRepository).delete(comment);

        commentService.deleteComment(comment.getId(), user);

        verify(commentRepository, times(1)).findById(comment.getId());
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteCommentShouldThrowResourceNotFoundException() {
        when(commentRepository.findById(comment.getId())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(comment.getId(), user));
    }

    @Test
    void deleteCommentShouldThrowRequestNotAllowedException() {
        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        comment.setUser(user2);

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        assertThrows(RequestNotAllowedException.class, () -> commentService.deleteComment(comment.getId(), user));
    }
}