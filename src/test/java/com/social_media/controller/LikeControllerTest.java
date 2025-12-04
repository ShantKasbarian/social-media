package com.social_media.controller;

import com.social_media.converter.LikeConverter;
import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.LikeDto;
import com.social_media.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LikeControllerTest {
    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeService likeService;

    @Mock
    private LikeConverter likeConverter;

    @Mock
    private Authentication authentication;

    private User user;

    private Post post;

    private Like like;

    private LikeDto likeDto;

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
        likeDto = new LikeDto(like.getId(), user.getId(), user.getUsername(), post.getId());
    }

    @Test
    void createLike() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(likeConverter.convertToModel(any(Like.class))).thenReturn(likeDto);
        when(likeService.createLike(any(User.class), any(UUID.class))).thenReturn(like);

        var response = likeController.createLike(authentication, post.getId());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(likeDto, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(likeConverter).convertToModel(any(Like.class));
        verify(likeService).createLike(any(User.class), any(UUID.class));
    }

    @Test
    void deleteLike() {
        when(authentication.getPrincipal()).thenReturn(user);
        doNothing().when(likeService).deleteLikeByPostId(any(UUID.class), any(UUID.class));

        likeController.deleteLike(authentication, post.getId());

        verify(authentication).getPrincipal();
        verify(likeService).deleteLikeByPostId(any(UUID.class), any(UUID.class));
    }
}
