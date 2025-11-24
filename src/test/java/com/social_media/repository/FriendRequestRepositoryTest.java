package com.social_media.repository;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.FriendshipStatus;
import com.social_media.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FriendRequestRepositoryTest {
    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;

    private User user2;

    private FriendRequest friendRequest;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(UUID.randomUUID().toString());
        user1.setEmail("someone@example.com");
        user1.setPassword("Password123+");
        user1.setUsername("johnDoe");
        user1.setName("John");
        user1.setLastname("Doe");

        user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setEmail("someone2@example.com");
        user2.setPassword("Password123+");
        user2.setUsername("JackDoe");
        user2.setName("Jack");
        user2.setLastname("Doe");

        userRepository.save(user1);
        userRepository.save(user2);

        friendRequest = new FriendRequest(UUID.randomUUID().toString(), user1, user2, FriendshipStatus.PENDING);
    }

    @Test
    void save() {
        FriendRequest response = friendRequestRepository.save(friendRequest);

        assertEquals(user1.getId(), response.getUser().getId());
        assertEquals(user2.getId(), response.getFriend().getId());
        assertEquals(FriendshipStatus.PENDING, response.getStatus());
    }

    @Test
    void existsByUserFriend() {
        friendRequestRepository.save(friendRequest);
        assertTrue(friendRequestRepository.existsByUser_idFriend_id(user1.getId(), user2.getId()));
    }

    @Test
    void findByUser_idFriend_id() {
        friendRequestRepository.save(friendRequest);

        FriendRequest response = friendRequestRepository
                .findByUser_idFriend_id(user1.getId(), user2.getId()).orElse(null);

        assertNotNull(response);
        assertEquals(friendRequest.getId(), response.getId());
    }

    @Test
    void findByUserFriend_FriendAndStatus() {
        friendRequestRepository.save(friendRequest);

        Page<FriendRequest> response = friendRequestRepository.findByUserFriend_FriendAndStatus(user2, friendRequest.getStatus(), PageRequest.of(0, 10));

        assertEquals(friendRequest.getId(), response.getContent().getFirst().getId());
    }

    @Test
    void findByFriend() {
        friendRequestRepository.save(friendRequest);

        Page<FriendRequest> response = friendRequestRepository.findByFriend(user2, PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(friendRequest.getId(), response.getContent().getFirst().getId());
        assertEquals(1, response.getContent().size());
    }
}