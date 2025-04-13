package com.social_media.services;

import com.social_media.converters.FriendRequestConverter;
import com.social_media.entities.FriendRequest;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.exceptions.RequestNotAllowedException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.exceptions.ResourceNotFoundException;
import com.social_media.models.FriendRequestDto;
import com.social_media.models.PageDto;
import com.social_media.repositories.FriendRequestRepository;
import com.social_media.repositories.UserRepository;
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

class FriendRequestServiceTest {
    @InjectMocks
    private FriendRequestService friendRequestService;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRequestConverter friendRequestConverter;

    private User user1;

    private User user2;

    private FriendRequest friendRequest;

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
        user1.setBlockedUsers(new ArrayList<>());

        user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setEmail("someone@example.com");
        user2.setPassword("Password123+");
        user2.setUsername("JackSmith");
        user2.setName("Jack");
        user2.setLastname("Smith");
        user2.setBlockedUsers(new ArrayList<>());

        friendRequest = new FriendRequest(UUID.randomUUID().toString(), user1, user2, FriendshipStatus.PENDING);
    }

    @Test
    void addFriend() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
        when(friendRequestRepository.existsByUser_idFriend_id(friendRequest.getUser().getId(), friendRequest.getFriend().getId())).thenReturn(false);
        when(friendRequestRepository.save(any(FriendRequest.class))).thenReturn(friendRequest);

        FriendRequest response = friendRequestService.addFriend(user2.getId(), user1);

        assertEquals(friendRequest.getUser().getId(), response.getUser().getId());
        assertEquals(FriendshipStatus.PENDING, response.getStatus());
        assertEquals(friendRequest.getFriend().getId(), response.getFriend().getId());
        verify(friendRequestRepository, times(1)).save(any(FriendRequest.class));
    }

    @Test
    void addFriendShouldThrowResourceNotFoundException() {
        when(userRepository.findById(user2.getId()))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> friendRequestService.addFriend(user2.getId(), user1));
    }

    @Test
    void addFriendShouldThrowResourceAlreadyExistsException() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(friendRequestRepository.existsByUser_idFriend_id(anyString(), anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> friendRequestService.addFriend(user2.getId(), user1));
    }

    @Test
    void acceptFriend() {
        when(friendRequestRepository.findById(friendRequest.getId())).thenReturn(Optional.ofNullable(friendRequest));
        when(friendRequestRepository.save(friendRequest)).thenReturn(friendRequest);

        FriendRequest response = friendRequestService.acceptFriend(friendRequest.getId(), user2);

        assertEquals(FriendshipStatus.ACCEPTED, response.getStatus());
        verify(friendRequestRepository, times(1)).save(friendRequest);
    }

    @Test
    void acceptFriendShouldThrowResourceNotFoundException() {
        when(friendRequestRepository.findById(friendRequest.getId()))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> friendRequestService.acceptFriend(friendRequest.getId(), user2));
    }

    @Test
    void getFriends() {
        List<FriendRequest> friendRequests = new ArrayList<>();
        friendRequest.setStatus(FriendshipStatus.ACCEPTED);
        friendRequests.add(friendRequest);

        Pageable pageable = PageRequest.of(0, 10);

        Page<FriendRequest> page = new PageImpl<>(friendRequests, pageable, friendRequests.size());

        when(friendRequestRepository.findByUserFriend_FriendAndStatus(user1, FriendshipStatus.ACCEPTED, pageable))
                .thenReturn(page);

        PageDto<FriendRequest, FriendRequestDto> response = friendRequestService.getFriends(user1, pageable);

        assertEquals(page.getContent().size(), response.getContent().size());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertEquals(page.getNumber(), response.getPageNo());
        verify(friendRequestRepository, times(1)).findByUserFriend_FriendAndStatus(user1, FriendshipStatus.ACCEPTED,pageable);
    }

    @Test
    void getPendingUsers() {
        List<FriendRequest> friendRequests = new ArrayList<>();
        friendRequests.add(friendRequest);

        Pageable pageable = PageRequest.of(0, 10);

        Page<FriendRequest> page = new PageImpl<>(friendRequests, pageable, friendRequests.size());

        when(friendRequestRepository.findByUserFriend_FriendAndStatus(user1, FriendshipStatus.PENDING, pageable))
                .thenReturn(page);

        PageDto<FriendRequest, FriendRequestDto> response = friendRequestService.getPendingUsers(user1, pageable);

        assertEquals(page.getContent().size(), response.getContent().size());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertEquals(page.getNumber(), response.getPageNo());
        verify(friendRequestRepository, times(1)).findByUserFriend_FriendAndStatus(user1, FriendshipStatus.PENDING, pageable);
    }
}