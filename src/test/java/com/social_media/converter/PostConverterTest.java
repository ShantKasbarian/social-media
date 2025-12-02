package com.social_media.converter;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostConverterTest {
    @InjectMocks
    private PostConverter postConverter;

    private Post post;

    private PostDto postDto;

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
    void convertToEntity() {
        Post post = postConverter.convertToEntity(postDto);

        assertNotNull(post);
        assertEquals(postDto.id(), post.getId());
        assertEquals(postDto.text(), post.getText());
    }

    @Test
    void convertToModel() {
        PostDto postDto = postConverter.convertToModel(post);

        assertNotNull(postDto);
        assertEquals(post.getId(), postDto.id());
        assertEquals(post.getUser().getId(), postDto.userId());
        assertEquals(post.getUser().getUsername(), postDto.username());
        assertEquals(post.getText(), postDto.text());
        assertEquals(post.getTime(), postDto.postedTime());
    }
}
