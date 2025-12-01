package com.social_media.repository;

import com.social_media.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private LikeRepository likeRepository;

    private User user;

    private Post post;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("someone@example.com");
        user.setPassword("Password123+");
        user.setUsername("johnDoe");
        user.setFirstname("John");
        user.setLastname("Doe");

        User user2 = new User();
        user2.setEmail("someone2@example.com");
        user2.setPassword("Password123+");
        user2.setUsername("JackDoe");
        user2.setFirstname("Jack");
        user2.setLastname("Doe");

        userRepository.save(user);
        userRepository.save(user2);

        post = new Post();
        post.setUser(user2);
        post.setTime(LocalDateTime.now());
        post.setText("some text");

        postRepository.save(post);

        FriendRequest friendRequest = new FriendRequest(user, user2, FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(friendRequest);
    }

    @Test
    void findByUserId() {
        Page<Post> response = postRepository.findByUserId(post.getUser().getId(), PageRequest.of(0, 10));

        assertNotNull(response);
        assertFalse(response.getContent().isEmpty());
        assertEquals(post.getId(), response.getContent().getFirst().getId());
        assertNotEquals(user.getId(), response.getContent().getFirst().getUser().getId());
    }

    @Test
    void findByUserAcceptedFriendRequests() {
        Page<Post> response = postRepository.findByUserAcceptedFriendRequests(user.getId(), PageRequest.of(0, 10));

        assertNotNull(response);
        assertFalse(response.getContent().isEmpty());
        assertEquals(post.getId(), response.getContent().getFirst().getId());
        assertNotEquals(user.getId(), response.getContent().getFirst().getUser().getId());
    }

    @Test
    void findByUserLikes() {
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        likeRepository.save(like);

        Page<Post> response = postRepository.findByUserLikes(user, PageRequest.of(0, 10));

        assertNotNull(response);
        assertFalse(response.getContent().isEmpty());
        assertEquals(post.getId(), response.getContent().getFirst().getId());
    }
}
