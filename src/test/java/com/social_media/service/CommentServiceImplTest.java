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
        post.setTitle("some text");

        comment = new Comment(
                UUID.randomUUID().toString(),
                "some text",
                LocalDateTime.now(),
                post,
                user
        );
    }

    @Test
    void createComment() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(friendRequestRepository.findByUserIdTargetUserId(anyString(), anyString())).thenReturn(Optional.empty());

        Comment response = commentService.createComment(comment.getContent(), , post.getId());

        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getContent(), response.getContent());
        assertEquals(comment.getUser().getId(), response.getUser().getId());
        assertEquals(comment.getPost().getId(), response.getPost().getId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createCommentShouldThrowInvalidProvidedInfoExceptionWhenContentIsNull() {
        assertThrows(InvalidProvidedInfoException.class, () -> commentService.createComment(null, , post.getId()));
    }

    @Test
    void createCommentShouldThrowInvalidProvidedInfoExceptionWhenContentIsEmpty() {
        assertThrows(InvalidProvidedInfoException.class, () -> commentService.createComment("", , post.getId()));
    }

    @Test
    void createCommentShouldThrowResourceNotFoundExceptionWhenPostIsNotFound() {
        when(postRepository.findById(anyString())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(comment.getContent(), , post.getId()));
    }

    @Test
    void createCommentShouldThrowRequestNotAllowedExceptionWhenUserIsBlocked() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setStatus(FriendshipStatus.BLOCKED);

        when(postRepository.findById(anyString())).thenReturn(Optional.ofNullable(post));
        when(friendRequestRepository.findByUserIdTargetUserId(anyString(), anyString()))
                .thenReturn(Optional.of(friendRequest));
        assertThrows(RequestNotAllowedException.class, () -> commentService.createComment(comment.getContent(), , post.getId()));
    }

    @Test
    void editCreateComment() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        String oldComment = comment.getContent();
        Comment response = commentService.updateComment(user, comment.getId());

        assertNotEquals(oldComment, response.getContent());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void editCreateCommentShouldThrowInvalidProvidedInfoExceptionWhenContentIsNull() {
        assertThrows(InvalidProvidedInfoException.class, () -> commentService.updateComment(user, comment.getId()));
    }

    @Test
    void editCreateCommentShouldThrowInvalidProvidedInfoExceptionWhenContentIsEmpty() {
        assertThrows(InvalidProvidedInfoException.class, () -> commentService.updateComment(user, comment.getId()));
    }

    @Test
    void updateCommentShouldThrowResourceNotFoundExceptionWhenCreateCommentIsNotFound() {
        when(commentRepository.findById(comment.getId())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(user, comment.getId()));
    }

    @Test
    void deleteCreateComment() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        doNothing().when(commentRepository).delete(comment);

        commentService.deleteComment(user, comment.getId());

        verify(commentRepository, times(1)).findById(comment.getId());
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteCreateCommentShouldThrowResourceNotFoundException() {
        when(commentRepository.findById(comment.getId())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(user, comment.getId()));
    }

    @Test
    void deleteCreateCommentShouldThrowRequestNotAllowedException() {
        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        comment.setUser(user2);

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.ofNullable(comment));
        assertThrows(RequestNotAllowedException.class, () -> commentService.deleteComment(user, comment.getId()));
    }
}