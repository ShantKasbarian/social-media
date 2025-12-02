package com.social_media.converter;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FriendRequestConverterTest {
    @InjectMocks
    private FriendRequestConverter friendRequestConverter;

    private FriendRequest friendRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("someone@example.com");
        user1.setPassword("Password123+");
        user1.setUsername("johnDoe");
        user1.setFirstname("John");
        user1.setLastname("Doe");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("someone@example.com");
        user2.setPassword("Password123+");
        user2.setUsername("JackSmith");
        user2.setFirstname("Jack");
        user2.setLastname("Smith");

        friendRequest = new FriendRequest(UUID.randomUUID(), user1, user2, FriendRequest.Status.PENDING);
    }

    @Test
    void convertToModel() {
        FriendRequestDto friendRequestDto = friendRequestConverter.convertToModel(friendRequest);

        assertNotNull(friendRequestDto);
        assertEquals(friendRequest.getId(), friendRequestDto.id());
        assertEquals(friendRequest.getUser().getId(), friendRequestDto.userId());
        assertEquals(friendRequest.getUser().getUsername(), friendRequestDto.username());
        assertEquals(friendRequest.getTargetUser().getId(), friendRequestDto.targetUserId());
        assertEquals(friendRequest.getTargetUser().getUsername(), friendRequestDto.targetUsername());
        assertEquals(friendRequest.getStatus(), friendRequestDto.status());
    }
}
