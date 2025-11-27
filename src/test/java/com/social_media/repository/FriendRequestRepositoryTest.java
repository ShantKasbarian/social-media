package com.social_media.repository;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
        user1.setEmail("someone@example.com");
        user1.setPassword("Password123+");
        user1.setUsername("johnDoe");
        user1.setFirstname("John");
        user1.setLastname("Doe");

        user2 = new User();
        user2.setEmail("someone2@example.com");
        user2.setPassword("Password123+");
        user2.setUsername("JackDoe");
        user2.setFirstname("Jack");
        user2.setLastname("Doe");

        userRepository.save(user1);
        userRepository.save(user2);

        friendRequest = new FriendRequest(user1, user2, FriendRequest.Status.PENDING);
        friendRequestRepository.save(friendRequest);
    }

    @Test
    void existsByUserIdTargetUserId() {
        assertTrue(friendRequestRepository.existsByUserIdTargetUserId(user1.getId(), user2.getId()));
    }

    @Test
    void existsByUserIdTargetUserIdStatus() {
        boolean exists = friendRequestRepository.existsByUserIdTargetUserIdStatus(
                user1.getId(), user2.getId(), friendRequest.getStatus()
        );

        assertTrue(exists);
    }

    @Test
    void findByUserIdTargetUserId() {
        FriendRequest friendRequest = friendRequestRepository.findByUserIdTargetUserId(user1.getId(), user2.getId()).get();

        assertNotNull(friendRequest);
        assertTrue(user1.getId().equals(friendRequest.getUser().getId()) || user2.getId().equals(friendRequest.getUser().getId()));
        assertTrue(user1.getId().equals(friendRequest.getTargetUser().getId()) || user2.getId().equals(friendRequest.getTargetUser().getId()));
    }

    @Test
    void findByUserStatus() {
        Page<FriendRequest> response = friendRequestRepository.findByUserStatus(user2, friendRequest.getStatus(),PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(friendRequest.getId(), response.getContent().getFirst().getId());
        assertEquals(1, response.getContent().size());
    }
}
