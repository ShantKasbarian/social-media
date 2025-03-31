package com.social_media.repositories;

import com.social_media.entities.Friend;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.entities.UserFriend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FriendRepositoryTest {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;

    private User user2;

    private Friend friend;

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

        friend = new Friend(UUID.randomUUID().toString(), new UserFriend(user1, user2), FriendshipStatus.PENDING);
    }

    @Test
    void save() {
        Friend response = friendRepository.save(friend);

        assertEquals(user1.getId(), response.getUserFriend().getUser().getId());
        assertEquals(user2.getId(), response.getUserFriend().getFriend().getId());
        assertEquals(FriendshipStatus.PENDING, response.getStatus());
    }

    @Test
    void existsByUserFriend() {
        friendRepository.save(friend);
        assertTrue(friendRepository.existsByUserFriend_user_id_friend_id(user1.getId(), user2.getId()));
    }

    @Test
    void findByUserFriend_FriendAndStatus() {
        friendRepository.save(friend);

        Page<Friend> response = friendRepository.findByUserFriend_FriendAndStatus(user2, friend.getStatus(), PageRequest.of(0, 10));

        assertEquals(friend.getId(), response.getContent().getFirst().getId());
    }
}