package com.social_media.repository;

import com.social_media.entity.Comment;
import com.social_media.entity.Post;
import com.social_media.entity.User;
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
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private User user;

    private Post post;

    private Comment comment;

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
        post.setTitle("some text");

        comment = new Comment(
                UUID.randomUUID().toString(),
                "some text",
                LocalDateTime.now(),
                post,
                user
        );

        userRepository.save(user);
        postRepository.save(post);
    }

    @Test
    void save() {
        Comment response = commentRepository.save(comment);

        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getContent(), response.getContent());
    }

    @Test
    void findById() {
        commentRepository.save(comment);

        Comment response = commentRepository.findById(comment.getId()).orElse(null);

        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getContent(), response.getContent());
    }

    @Test
    void delete() {
        commentRepository.save(comment);

        commentRepository.delete(comment);

        assertEquals(Optional.empty(), commentRepository.findById(comment.getId()));
    }

    @Test
    void findByPostId() {
        commentRepository.save(comment);

        Page<Comment> comments = commentRepository.findByPostId(post.getId(), PageRequest.of(0, 10));

        assertNotNull(comments.getContent().getFirst());
        assertEquals(comment.getId(), comments.getContent().getFirst().getId());
    }
}