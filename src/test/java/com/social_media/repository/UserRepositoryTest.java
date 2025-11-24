package com.social_media.repository;

import com.social_media.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("someone@example.com");
        user.setPassword("Password123+");
        user.setUsername("johnDoe");
        user.setName("John");
        user.setLastname("Doe");
    }

    @Test
    void save() {
        User response = userRepository.save(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void findById() {
        userRepository.save(user);

        User response = userRepository.findById(user.getId()).orElse(null);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void findByUsername() {
        userRepository.save(user);

        User response = userRepository.findByUsername(user.getUsername()).orElse(null);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void findByEmail() {
        userRepository.save(user);

        User response = userRepository.findByEmail(user.getEmail()).orElse(null);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void existsByEmail() {
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail(user.getEmail()));
    }

    @Test
    void existsByUsername() {
        userRepository.save(user);

        assertTrue(userRepository.existsByUsername(user.getUsername()));
    }

    @Test
    void findByUsernameContainingIgnoreCase() {
        userRepository.save(user);

        Page<User> response = userRepository.findByUsernameContainingIgnoreCase("J", PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.asc("username"))
        ));

        assertFalse(response.isEmpty());
        assertEquals(user.getId(), response.getContent().getFirst().getId());
        assertEquals(user.getEmail(), response.getContent().getFirst().getEmail());
    }
}