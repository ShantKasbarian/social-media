package com.social_media.repositories;

import com.social_media.entities.Comment;
import com.social_media.entities.Post;
import com.social_media.entities.User;
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
    void setup() {
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
    void save() {
        userRepository.save(user);
        postRepository.save(post);
        Comment response = commentRepository.save(comment);

        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getContent(), response.getContent());
    }

    @Test
    void findById() {
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        Comment response = commentRepository.findById(comment.getId()).orElse(null);

        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getContent(), response.getContent());
    }

    @Test
    void delete() {
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        commentRepository.delete(comment);

        assertEquals(Optional.empty(), commentRepository.findById(comment.getId()));
    }

    @Test
    void findByPost_id() {
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);

        Page<Comment> comments = commentRepository.findByPost_id(post.getId(), PageRequest.of(0, 10));

        assertNotNull(comments.getContent().getFirst());
        assertEquals(comment.getId(), comments.getContent().getFirst().getId());
    }
}