package com.social_media.controllers;

import com.social_media.converters.FriendRequestConverter;
import com.social_media.entities.FriendRequest;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.models.FriendRequestDto;
import com.social_media.models.PageDto;
import com.social_media.services.FriendRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
        user1.setId(UUID.randomUUID().toString());
        user1.setEmail("someone@example.com");
        user1.setPassword("Password123+");
        user1.setUsername("johnDoe");
        user1.setName("John");
        user1.setLastname("Doe");

        user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setEmail("someone@example.com");
        user2.setPassword("Password123+");
        user2.setUsername("JackSmith");
        user2.setName("Jack");
        user2.setLastname("Smith");

        friendRequest = new FriendRequest(UUID.randomUUID().toString(), user1, user2, FriendshipStatus.PENDING);

        friendRequestDto = new FriendRequestDto(
                friendRequest.getId(),
                friendRequest.getUser().getId(),
                friendRequest.getUser().getUsername(),
                friendRequest.getFriend().getId(),
                friendRequest.getFriend().getUsername(),
                friendRequest.getStatus().toString()
        );
    }

    @Test
    void addFriend() {
        when(friendRequestConverter.convertToModel(any(FriendRequest.class))).thenReturn(friendRequestDto);
        when(friendRequestService.addFriend(anyString(), any(User.class))).thenReturn(friendRequest);
        when(authentication.getPrincipal()).thenReturn(user1);

        ResponseEntity<FriendRequestDto> response = friendRequestController.addFriend(authentication, user2.getId());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(friendRequest.getId(), response.getBody().id());
    }

    @Test
    void acceptFriend() {
        when(friendRequestConverter.convertToModel(any(FriendRequest.class))).thenReturn(friendRequestDto);
        when(friendRequestService.acceptFriend(friendRequest.getId(), user2)).thenReturn(friendRequest);
        when(authentication.getPrincipal()).thenReturn(user2);

        ResponseEntity<FriendRequestDto> response = friendRequestController.acceptFriend(authentication, friendRequest.getId());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friendRequest.getId(), response.getBody().id());
    }

    @Test
    void getFriends() {
        friendRequest.setStatus(FriendshipStatus.ACCEPTED);
        List<FriendRequest> content = new ArrayList<>();
        content.add(friendRequest);

        Page<FriendRequest> page = new PageImpl<>(content);
        PageDto<FriendRequest, FriendRequestDto> pageDto = new PageDto<>(page, friendRequestConverter);

        when(friendRequestService.getFriends(user1, PageRequest.of(0, 10)))
                .thenReturn(pageDto);
        when(authentication.getPrincipal()).thenReturn(user1);

        ResponseEntity<PageDto<FriendRequest, FriendRequestDto>> response = friendRequestController.getFriends(authentication, 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page.getContent().size(), response.getBody().getContent().size());
    }

    @Test
    void getPendingUsers() {
        List<FriendRequest> content = new ArrayList<>();
        content.add(friendRequest);

        Page<FriendRequest> page = new PageImpl<>(content);
        PageDto<FriendRequest, FriendRequestDto> pageDto = new PageDto<>(page, friendRequestConverter);

        when(friendRequestService.getPendingUsers(user1, PageRequest.of(0, 10)))
                .thenReturn(pageDto);
        when(authentication.getPrincipal()).thenReturn(user1);

        ResponseEntity<PageDto<FriendRequest, FriendRequestDto>> response = friendRequestController.getPendingUsers(authentication, 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page.getContent().size(), response.getBody().getContent().size());
    }

    @Test
    void declineFriendRequest() {
        when(friendRequestConverter.convertToModel(friendRequest)).thenReturn(friendRequestDto);
        when(friendRequestService.declineFriendRequest(anyString(), any(User.class))).thenReturn(friendRequest);
        when(authentication.getPrincipal()).thenReturn(user2);

        ResponseEntity<FriendRequestDto> response = friendRequestController.declineFriendRequest(friendRequest.getId(), authentication);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friendRequest.getId(), response.getBody().id());
    }
}