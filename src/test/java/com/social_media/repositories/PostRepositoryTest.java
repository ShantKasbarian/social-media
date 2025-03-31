package com.social_media.repositories;

import com.social_media.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private LikeRepository likeRepository;

    private User user;

    private User user2;

    private Post post;

    private Friend friend;

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

        user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setEmail("someone2@example.com");
        user2.setPassword("Password123+");
        user2.setUsername("JackDoe");
        user2.setName("Jack");
        user2.setLastname("Doe");

        userRepository.save(user2);

        friend = new Friend(UUID.randomUUID().toString(), new UserFriend(user, user2), FriendshipStatus.ACCEPTED);

        userRepository.save(user);
        friendRepository.save(friend);
    }

    @Test
    void save() {
        Post response = postRepository.save(post);

        assertEquals(post.getId(), response.getId());
        assertEquals(post.getTitle(), response.getTitle());
    }

    @Test
    void findById() {
        Post savedPost = postRepository.save(post);

        Post response = postRepository.findById(savedPost.getId()).orElse(null);

        assertNotNull(response);
        assertEquals(savedPost.getTitle(), response.getTitle());
    }

    @Test
    void delete() {
        Post savedPost = postRepository.save(post);

        postRepository.delete(post);

        assertEquals(Optional.empty(), postRepository.findById(savedPost.getId()));
    }

    @Test
    void findByUser() {
        postRepository.save(post);

        Page<Post> response = postRepository.findByUser(user, PageRequest.of(0, 10));

        assertEquals(post.getId(), response.getContent().getFirst().getId());
        assertEquals(post.getTitle(), response.getContent().getFirst().getTitle());
    }

    @Test
    void findByUser_Friends() {
        postRepository.save(post);

        Page<Post> response = postRepository.findByUser_Friends(user2.getId(), PageRequest.of(0, 10));

        assertFalse(response.getContent().isEmpty());
        assertEquals(post.getId(), response.getContent().getFirst().getId());
        assertEquals(user.getId(), response.getContent().getFirst().getUser().getId());
    }

    @Test
    void findByLikesUser() {
        postRepository.save(post);

        likeRepository.save(new Like(UUID.randomUUID().toString(), user2, post));

        Page<Post> response = postRepository.findByLikesUser(user2, PageRequest.of(0, 10));

        assertFalse(response.getContent().isEmpty());
        assertEquals(post.getId(), response.getContent().getFirst().getId());
    }
}