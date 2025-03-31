package com.social_media.repositories;

import com.social_media.entities.Like;
import com.social_media.entities.Post;
import com.social_media.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LikeRepositoryTest {
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private Like like;

    private User user;

    private Post post;

    @BeforeEach
    void setUp() {
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

        userRepository.save(user);
        postRepository.save(post);

        like = new Like(UUID.randomUUID().toString(), user, post);
    }

    @Test
    void save() {
        Like response = likeRepository.save(like);

        assertEquals(like.getId(), response.getId());
        assertEquals(post.getId(), response.getPost().getId());
        assertEquals(user.getId(), response.getUser().getId());
    }

    @Test
    void existsByPostAndUser() {
        likeRepository.save(like);

        assertTrue(likeRepository.existsByPostAndUser(post, user));
    }

    @Test
    void delete() {
        Like savedLike = likeRepository.save(like);

        likeRepository.delete(like);

        assertEquals(Optional.empty(), likeRepository.findById(savedLike.getId()));
    }
}