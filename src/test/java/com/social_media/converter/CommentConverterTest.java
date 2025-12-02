package com.social_media.converter;

import com.social_media.entity.Comment;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentConverterTest {
    @InjectMocks
    private CommentConverter commentConverter;

    @Mock
    private PostService postService;

    private Post post;

    private Comment comment;

    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
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
    void convertToEntity() {
        when(postService.getPostById(any(UUID.class))).thenReturn(post);

        Comment comment = commentConverter.convertToEntity(commentDto);

        assertNotNull(comment);
        assertEquals(commentDto.id(), comment.getId());
        assertEquals(commentDto.postId(), comment.getPost().getId());
        assertEquals(commentDto.text(), comment.getText());
        verify(postService).getPostById(any(UUID.class));
    }

    @Test
    void convertToModel() {
        CommentDto commentDto = commentConverter.convertToModel(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.id());
        assertEquals(comment.getPost().getId(), commentDto.postId());
        assertEquals(comment.getText(), commentDto.text());
        assertEquals(comment.getUser().getId(), commentDto.userId());
        assertEquals(comment.getUser().getUsername(), commentDto.username());
        assertEquals(comment.getTime(), commentDto.commentedTime());
    }
}
