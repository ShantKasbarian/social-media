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
        user.setEmail("someone@example.com");
        user.setPassword("Password123+");
        user.setUsername("johnDoe");
        user.setFirstname("John");
        user.setLastname("Doe");

        post = new Post();
        post.setUser(user);
        post.setTime(LocalDateTime.now());
        post.setText("some text");

        comment = new Comment();
        comment.setText("some text");
        comment.setTime(LocalDateTime.now());
        comment.setPost(post);
        comment.setUser(user);

        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);
    }

    @Test
    void findByPostId() {
        Page<Comment> comments = commentRepository.findByPostId(post.getId(), PageRequest.of(0, 10));
        Comment response = comments.getContent().getFirst();

        assertEquals(comment.getText(), response.getText());
        assertEquals(comment.getTime(), response.getTime());
        assertEquals(comment.getUser(), response.getUser());
        assertEquals(comment.getPost(), response.getPost());
    }
}
