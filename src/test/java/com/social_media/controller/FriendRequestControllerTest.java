package com.social_media.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.social_media.converter.FriendRequestConverter;
import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import com.social_media.service.FriendRequestService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

class FriendRequestControllerTest {
  @InjectMocks private FriendRequestController friendRequestController;

  @Mock private FriendRequestService friendRequestService;

  @Mock private FriendRequestConverter friendRequestConverter;

  @Mock private Authentication authentication;

  private User user1;

  private User user2;

  private FriendRequest friendRequest;

  private FriendRequestDto friendRequestDto;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user1 = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    user1.setId(UUID.randomUUID());

    user2 = new User("emily.smith@example.com", "Password123+", "Emily.Smith", "Emily", "Smith");
    user2.setId(UUID.randomUUID());

    friendRequest = new FriendRequest(user1, user2, FriendRequest.Status.PENDING);
    friendRequest.setId(UUID.randomUUID());

    friendRequestDto =
        new FriendRequestDto(
            friendRequest.getId(),
            friendRequest.getUser().getId(),
            friendRequest.getUser().getUsername(),
            friendRequest.getTargetUser().getId(),
            friendRequest.getTargetUser().getUsername(),
            friendRequest.getStatus());
  }

  @Test
  void createFriendRequest() {
    when(authentication.getPrincipal()).thenReturn(user1);
    when(friendRequestConverter.convertToModel(any(FriendRequest.class)))
        .thenReturn(friendRequestDto);
    when(friendRequestService.create(any(User.class), any(UUID.class))).thenReturn(friendRequest);

    var response = friendRequestController.createFriendRequest(authentication, user2.getId());

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(friendRequestDto, response.getBody());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    verify(authentication).getPrincipal();
    verify(friendRequestConverter).convertToModel(any(FriendRequest.class));
    verify(friendRequestService).create(any(User.class), any(UUID.class));
  }

  @Test
  void acceptFriendRequest() {
    when(authentication.getPrincipal()).thenReturn(user1);
    when(friendRequestConverter.convertToModel(any(FriendRequest.class)))
        .thenReturn(friendRequestDto);
    when(friendRequestService.acceptFriendRequest(any(User.class), any(UUID.class)))
        .thenReturn(friendRequest);

    var response =
        friendRequestController.acceptFriendRequest(authentication, friendRequest.getId());

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(friendRequestDto, response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(authentication).getPrincipal();
    verify(friendRequestConverter).convertToModel(any(FriendRequest.class));
    verify(friendRequestService).acceptFriendRequest(any(User.class), any(UUID.class));
  }

  @Test
  void deleteFriendRequest() {
    when(authentication.getPrincipal()).thenReturn(user1);
    doNothing().when(friendRequestService).delete(any(User.class), any(UUID.class));

    var response =
        friendRequestController.deleteFriendRequest(authentication, friendRequest.getId());

    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(authentication).getPrincipal();
    verify(friendRequestService).delete(any(User.class), any(UUID.class));
  }

  @Test
  void getFriendRequestsByStatus() {
    List<FriendRequest> friendRequests = new ArrayList<>();
    friendRequests.add(friendRequest);

    Page<FriendRequest> page = new PageImpl<>(friendRequests);

    when(authentication.getPrincipal()).thenReturn(user1);
    when(friendRequestService.findByUserAndStatus(
            any(User.class), any(FriendRequest.Status.class), any(Pageable.class)))
        .thenReturn(page);

    var response =
        friendRequestController.getFriendRequestsByStatus(
            authentication, FriendRequest.Status.PENDING, 0, 10);

    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(authentication).getPrincipal();
    verify(friendRequestService)
        .findByUserAndStatus(any(User.class), any(FriendRequest.Status.class), any(Pageable.class));
  }
}
