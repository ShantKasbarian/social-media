package com.social_media.controller;

import com.social_media.converter.FriendRequestConverter;
import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import com.social_media.service.FriendRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FriendRequestControllerTest {
    @InjectMocks
    private FriendRequestController friendRequestController;

    @Mock
    private FriendRequestService friendRequestService;

    @Mock
    private FriendRequestConverter friendRequestConverter;

    @Mock
    private Authentication authentication;

    private User user1;

    private User user2;

    private FriendRequest friendRequest;

    private FriendRequestDto friendRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("someone@example.com");
        user1.setPassword("Password123+");
        user1.setUsername("johnDoe");
        user1.setFirstname("John");
        user1.setLastname("Doe");

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("someone@example.com");
        user2.setPassword("Password123+");
        user2.setUsername("JackSmith");
        user2.setFirstname("Jack");
        user2.setLastname("Smith");

        friendRequest = new FriendRequest(UUID.randomUUID(), user1, user2, FriendRequest.Status.PENDING);

        friendRequestDto = new FriendRequestDto(
                friendRequest.getId(),
                friendRequest.getUser().getId(),
                friendRequest.getUser().getUsername(),
                friendRequest.getTargetUser().getId(),
                friendRequest.getTargetUser().getUsername(),
                friendRequest.getStatus()
        );
    }

    @Test
    void createFriendRequest() {
        when(authentication.getPrincipal()).thenReturn(user1);
        when(friendRequestConverter.convertToModel(any(FriendRequest.class)))
                .thenReturn(friendRequestDto);
        when(friendRequestService.createFriendRequest(any(User.class), any(UUID.class)))
                .thenReturn(friendRequest);

        var response = friendRequestController.createFriendRequest(authentication, user2.getId());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(friendRequestDto, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(friendRequestConverter).convertToModel(any(FriendRequest.class));
        verify(friendRequestService).createFriendRequest(any(User.class), any(UUID.class));
    }

    @Test
    void updateFriendRequestStatus() {
        when(authentication.getPrincipal()).thenReturn(user1);
        when(friendRequestConverter.convertToModel(any(FriendRequest.class)))
                .thenReturn(friendRequestDto);
        when(friendRequestService.updateFriendRequestStatus(any(User.class), any(UUID.class), any(FriendRequest.Status.class)))
                .thenReturn(friendRequest);

        var response = friendRequestController.updateFriendRequestStatus(authentication, friendRequestDto);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(friendRequestDto, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(friendRequestConverter).convertToModel(any(FriendRequest.class));
        verify(friendRequestService).updateFriendRequestStatus(any(User.class), any(UUID.class), any(FriendRequest.Status.class));
    }

    @Test
    void deleteFriendRequest() {
        when(authentication.getPrincipal()).thenReturn(user1);
        doNothing().when(friendRequestService).deleteFriendRequest(any(User.class), any(UUID.class));

        friendRequestController.deleteFriendRequest(authentication, friendRequest.getId());

        verify(authentication).getPrincipal();
        verify(friendRequestService).deleteFriendRequest(any(User.class), any(UUID.class));
    }

    @Test
    void getFriendRequestsByStatus() {
        List<FriendRequest> friendRequests = new ArrayList<>();
        friendRequests.add(friendRequest);

        Page<FriendRequest> page = new PageImpl<>(friendRequests);

        when(authentication.getPrincipal()).thenReturn(user1);
        when(friendRequestService.getFriendRequestsByStatus(any(User.class), any(FriendRequest.Status.class), any(Pageable.class)))
                .thenReturn(page);

        var response = friendRequestController.getFriendRequestsByStatus(authentication, FriendRequest.Status.PENDING, 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(friendRequestService).getFriendRequestsByStatus(any(User.class), any(FriendRequest.Status.class), any(Pageable.class));
    }
}
