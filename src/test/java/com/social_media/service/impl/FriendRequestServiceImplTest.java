package com.social_media.service.impl;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendRequestServiceImplTest {
    @InjectMocks
    private FriendRequestServiceImpl friendRequestService;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private UserRepository userRepository;

    private User user1;

    private User user2;

    private FriendRequest friendRequest;

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
    }

    @Test
    void createFriendRequest() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
        when(friendRequestRepository.existsByUserIdTargetUserId(user1.getId(), user2.getId()))
                .thenReturn(false);
        when(friendRequestRepository.save(any(FriendRequest.class)))
                .thenReturn(friendRequest);

        var response = friendRequestService.createFriendRequest(user1, user2.getId());

        assertEquals(friendRequest.getUser().getId(), response.getUser().getId());
        assertEquals(FriendRequest.Status.PENDING, response.getStatus());
        assertEquals(friendRequest.getUser(), response.getUser());
        assertEquals(friendRequest.getTargetUser(), response.getTargetUser());
        verify(userRepository).findById(user2.getId());
        verify(friendRequestRepository).existsByUserIdTargetUserId(user1.getId(), user2.getId());
        verify(friendRequestRepository).save(any(FriendRequest.class));
    }

    @Test
    void createFriendRequestShouldThrowResourceNotFoundExceptionWhenTargetUserNotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> friendRequestService.createFriendRequest(user1, user2.getId()));
    }

    @Test
    void createFriendRequestShouldThrowResourceAlreadyExistsExceptionWhenFriendRequestExist() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(user2));
        when(friendRequestRepository.existsByUserIdTargetUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> friendRequestService.createFriendRequest(user1, user2.getId()));
    }

    @Test
    void updateFriendRequestStatus() {
        FriendRequest.Status targetStatus = FriendRequest.Status.ACCEPTED;

        when(friendRequestRepository.findById(any(UUID.class)))
                .thenReturn(Optional.ofNullable(friendRequest));
        when(friendRequestRepository.save(any(FriendRequest.class)))
                .thenReturn(friendRequest);

        var response = friendRequestService.updateFriendRequestStatus(friendRequest.getTargetUser(), friendRequest.getId(), targetStatus);

        assertNotNull(response);
        assertEquals(targetStatus, response.getStatus());
        verify(friendRequestRepository).findById(any(UUID.class));
        verify(friendRequestRepository).save(any(FriendRequest.class));
    }

    @Test
    void updateFriendRequestStatusShouldThrowResourceNotFoundExceptionWhenFriendRequestIsNotFound() {
        when(friendRequestRepository.findById(friendRequest.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> friendRequestService.updateFriendRequestStatus(user1, friendRequest.getId(), FriendRequest.Status.BLOCKED));
    }

    @Test
    void updateFriendRequestStatusShouldThrowRequestNotAllowedExceptionWhenUserIdNotEqualsTargetUserIdAndTargetStatusIsACCEPTED() {
        when(friendRequestRepository.findById(friendRequest.getId()))
                .thenReturn(Optional.ofNullable(friendRequest));

        assertThrows(RequestNotAllowedException.class, () -> friendRequestService.updateFriendRequestStatus(friendRequest.getUser(), friendRequest.getId(), FriendRequest.Status.ACCEPTED));
    }

    @Test
    void updateFriendRequestStatusShouldThrowRequestNotAllowedExceptionWhenFriendRequestIsBlocked() {
        friendRequest.setStatus(FriendRequest.Status.BLOCKED);

        when(friendRequestRepository.findById(friendRequest.getId()))
                .thenReturn(Optional.ofNullable(friendRequest));

        assertThrows(RequestNotAllowedException.class, () -> friendRequestService.updateFriendRequestStatus(friendRequest.getUser(), friendRequest.getId(), FriendRequest.Status.ACCEPTED));
    }

    @Test
    void deleteFriendRequest() {
        friendRequest.setUser(user1);
        friendRequest.setTargetUser(user2);
        friendRequest.setStatus(FriendRequest.Status.PENDING);

        when(friendRequestRepository.findById(any(UUID.class)))
                .thenReturn(Optional.ofNullable(friendRequest));
        doNothing().when(friendRequestRepository).delete(any(FriendRequest.class));

        friendRequestService.deleteFriendRequest(user1, friendRequest.getId());

        verify(friendRequestRepository).findById(any(UUID.class));
        verify(friendRequestRepository).delete(any(FriendRequest.class));
    }

    @Test
    void deleteFriendRequestShouldThrowResourceNotFoundExceptionWhenFriendRequestNotFound() {
        when(friendRequestRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> friendRequestService.deleteFriendRequest(user1, friendRequest.getId()));
    }

    @Test
    void deleteFriendRequestShouldThrowRequestNotAllowedExceptionWhenUserIdNotEqualsSenderIdOrTargetUserId() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(friendRequestRepository.findById(any(UUID.class)))
                .thenReturn(Optional.ofNullable(friendRequest));

        assertThrows(RequestNotAllowedException.class, () -> friendRequestService.deleteFriendRequest(user, friendRequest.getId()));
    }

    @Test
    void deleteFriendRequestShouldThrowRequestNotAllowedExceptionWhenFriendRequestIsBlocked() {
        friendRequest.setStatus(FriendRequest.Status.BLOCKED);

        when(friendRequestRepository.findById(any(UUID.class)))
                .thenReturn(Optional.ofNullable(friendRequest));

        assertThrows(RequestNotAllowedException.class, () -> friendRequestService.deleteFriendRequest(user1, friendRequest.getId()));
    }

    @Test
    void getFriendRequestsByUserStatus() {
        List<FriendRequest> friendRequests = new ArrayList<>();
        friendRequests.add(friendRequest);
        Page<FriendRequest> page = new PageImpl<>(friendRequests);
        Pageable pageable = PageRequest.of(0, 10);

        when(friendRequestRepository.findByUserStatus(any(User.class), any(FriendRequest.Status.class), any(Pageable.class)))
                .thenReturn(page);

        var response = friendRequestService.getFriendRequestsByUserStatus(user1, FriendRequest.Status.PENDING, pageable);

        assertNotNull(response);
        assertEquals(page, response);
    }
}
