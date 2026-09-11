package com.social_media.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.UserRepository;
import com.social_media.service.UserBlockService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class FriendRequestServiceImplTest {
  private static final String USER_NOT_FOUND_MESSAGE = "user not found";

  private static final String FRIEND_REQUEST_ALREADY_EXISTS_MESSAGE =
      "friend request already exists";

  private static final String UNABLE_TO_UPDATE_FRIEND_REQUEST_STATUS_MESSAGE =
      "cannot update friendRequest status";

  private static final String UNABLE_TO_DELETE_FRIEND_REQUEST_MESSAGE =
      "cannot delete friend request";

  private static final String FRIEND_REQUEST_NOT_FOUND_MESSAGE = "friend request not found";

  @InjectMocks private FriendRequestServiceImpl friendRequestService;

  @Mock private FriendRequestRepository friendRequestRepository;

  @Mock private UserBlockService userBlockService;

  @Mock private UserRepository userRepository;

  private User user1;

  private User user2;

  private FriendRequest friendRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user1 = new User("john.doe@example.com", "Password123+", "John.Doe", "John", "Doe");
    user1.setId(UUID.randomUUID());

    user2 = new User("emily.smith@example.com", "Password123+", "Emily.Smith", "Emily", "Smith");
    user2.setId(UUID.randomUUID());

    friendRequest = new FriendRequest(user1, user2, FriendRequest.Status.PENDING);
    friendRequest.setId(UUID.randomUUID());
  }

  @Test
  void create() {
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(user2));
    when(friendRequestRepository.existsByUserIdTargetUserId(any(UUID.class), any(UUID.class)))
        .thenReturn(false);
    doNothing().when(userBlockService).checkBlockRelationship(any(UUID.class), any(UUID.class));
    when(friendRequestRepository.save(any(FriendRequest.class))).thenReturn(friendRequest);

    var response = friendRequestService.create(user1, user2.getId());

    assertNotNull(response);
    assertEquals(friendRequest.getUser().getId(), response.getUser().getId());
    assertEquals(FriendRequest.Status.PENDING, response.getStatus());
    assertEquals(friendRequest.getUser(), response.getUser());
    assertEquals(friendRequest.getTargetUser(), response.getTargetUser());
    verify(userRepository).findById(any(UUID.class));
    verify(friendRequestRepository).existsByUserIdTargetUserId(any(UUID.class), any(UUID.class));
    verify(userBlockService).checkBlockRelationship(any(UUID.class), any(UUID.class));
    verify(friendRequestRepository).save(any(FriendRequest.class));
  }

  @Test
  void createShouldThrowResourceNotFoundExceptionWhenTargetUserIsNotFound() {
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> friendRequestService.create(user1, user2.getId()));
    assertEquals(USER_NOT_FOUND_MESSAGE, exception.getMessage());
  }

  @Test
  void createShouldThrowResourceAlreadyExistsExceptionWhenFriendRequestExists() {
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(user2));
    when(friendRequestRepository.existsByUserIdTargetUserId(any(UUID.class), any(UUID.class)))
        .thenReturn(true);

    Exception exception =
        assertThrows(
            ResourceAlreadyExistsException.class,
            () -> friendRequestService.create(user1, user2.getId()));
    assertEquals(FRIEND_REQUEST_ALREADY_EXISTS_MESSAGE, exception.getMessage());
  }

  @Test
  void acceptFriendRequest() {
    when(friendRequestRepository.findById(any(UUID.class)))
        .thenReturn(Optional.ofNullable(friendRequest));
    doNothing().when(userBlockService).checkBlockRelationship(any(UUID.class), any(UUID.class));

    var response =
        friendRequestService.acceptFriendRequest(
            friendRequest.getTargetUser(), friendRequest.getId());

    assertNotNull(response);
    assertEquals(FriendRequest.Status.ACCEPTED, response.getStatus());
    verify(friendRequestRepository).findById(any(UUID.class));
  }

  @Test
  void acceptFriendRequestShouldThrowResourceNotFoundExceptionWhenFriendRequestIsNotFound() {
    when(friendRequestRepository.findById(friendRequest.getId())).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> friendRequestService.acceptFriendRequest(user1, friendRequest.getId()));
    assertEquals(FRIEND_REQUEST_NOT_FOUND_MESSAGE, exception.getMessage());
  }

  @Test
  void
      acceptFriendRequestShouldThrowRequestNotAllowedExceptionWhenStatusIsPendingAndCurrentUserIsNotTargetUser() {
    when(friendRequestRepository.findById(friendRequest.getId()))
        .thenReturn(Optional.ofNullable(friendRequest));

    Exception exception =
        assertThrows(
            RequestNotAllowedException.class,
            () ->
                friendRequestService.acceptFriendRequest(
                    friendRequest.getUser(), friendRequest.getId()));
    assertEquals(UNABLE_TO_UPDATE_FRIEND_REQUEST_STATUS_MESSAGE, exception.getMessage());
  }

  @Test
  void delete() {
    friendRequest.setUser(user1);
    friendRequest.setTargetUser(user2);
    friendRequest.setStatus(FriendRequest.Status.PENDING);

    when(friendRequestRepository.findById(any(UUID.class)))
        .thenReturn(Optional.ofNullable(friendRequest));
    doNothing().when(friendRequestRepository).delete(any(FriendRequest.class));

    friendRequestService.delete(user1, friendRequest.getId());

    verify(friendRequestRepository).findById(any(UUID.class));
    verify(friendRequestRepository).delete(any(FriendRequest.class));
  }

  @Test
  void deleteShouldThrowResourceNotFoundExceptionWhenFriendRequestIsNotFound() {
    when(friendRequestRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> friendRequestService.delete(user1, friendRequest.getId()));
    assertEquals(FRIEND_REQUEST_NOT_FOUND_MESSAGE, exception.getMessage());
  }

  @Test
  void deleteShouldThrowRequestNotAllowedExceptionWhenUserIdIsNotEqualToSenderIdOrTargetUserId() {
    User user = new User("emma.smith@example.com", "Password123+", "Emma.Smith", "Emma", "Smith");
    user.setId(UUID.randomUUID());

    when(friendRequestRepository.findById(any(UUID.class)))
        .thenReturn(Optional.ofNullable(friendRequest));

    Exception exception =
        assertThrows(
            RequestNotAllowedException.class,
            () -> friendRequestService.delete(user, friendRequest.getId()));
    assertEquals(UNABLE_TO_DELETE_FRIEND_REQUEST_MESSAGE, exception.getMessage());
  }

  @Test
  void findByUserAndStatus() {
    List<FriendRequest> friendRequests = new ArrayList<>();
    friendRequests.add(friendRequest);
    Page<FriendRequest> page = new PageImpl<>(friendRequests);
    Pageable pageable = PageRequest.of(0, 10);

    when(friendRequestRepository.findByUserStatus(
            any(User.class), any(FriendRequest.Status.class), any(Pageable.class)))
        .thenReturn(page);

    var response =
        friendRequestService.findByUserAndStatus(user1, FriendRequest.Status.PENDING, pageable);

    assertNotNull(response);
    assertEquals(page, response);
  }
}
