package com.social_media.services;

import com.social_media.converters.FriendConverter;
import com.social_media.entities.Friend;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.entities.UserFriend;
import com.social_media.exceptions.RequestNotAllowedException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.exceptions.ResourceNotFoundException;
import com.social_media.models.FriendDto;
import com.social_media.models.PageDto;
import com.social_media.repositories.FriendRepository;
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

class FriendServiceTest {
    @InjectMocks
    private FriendService friendService;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendConverter friendConverter;

    private User user1;

    private User user2;

    private Friend friend;

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

        friend = new Friend(UUID.randomUUID().toString(), new UserFriend(user1, user2), FriendshipStatus.PENDING);
    }

    @Test
    void addFriend() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.ofNullable(user2));
        when(friendRepository.existsByUserFriend(friend.getUserFriend())).thenReturn(false);
        when(friendRepository.save(any(Friend.class))).thenReturn(friend);

        Friend response = friendService.addFriend(user2.getId(), user1);

        assertEquals(friend.getUserFriend().getUser().getId(), response.getUserFriend().getUser().getId());
        assertEquals(FriendshipStatus.PENDING, response.getStatus());
        assertEquals(friend.getUserFriend().getFriend().getId(), response.getUserFriend().getFriend().getId());
    }

    @Test
    void addFriendShouldThrowResourceNotFoundException() {
        when(userRepository.findById(user2.getId()))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> friendService.addFriend(user2.getId(), user1));
    }

    @Test
    void addFriendShouldThrowResourceAlreadyExistsException() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(friendRepository.existsByUserFriend(any(UserFriend.class))).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> friendService.addFriend(user2.getId(), user1));
    }

    @Test
    void acceptFriend() {
        when(friendRepository.findById(friend.getId())).thenReturn(Optional.ofNullable(friend));
        when(friendRepository.save(friend)).thenReturn(friend);

        Friend response = friendService.acceptFriend(friend.getId(), user2);

        assertEquals(FriendshipStatus.ACCEPTED, response.getStatus());
        verify(friendRepository, times(1)).save(friend);
    }

    @Test
    void acceptFriendShouldThrowResourceNotFoundException() {
        when(friendRepository.findById(friend.getId()))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> friendService.acceptFriend(friend.getId(), user2));
    }

    @Test
    void blockFriend() {
        when(friendRepository.findById(friend.getId()))
                .thenReturn(Optional.ofNullable(friend));
        when(friendRepository.save(friend)).thenReturn(friend);

        Friend response = friendService.blockFriend(friend.getId(), user1);

        assertEquals(friend.getId(), response.getId());
        assertEquals(FriendshipStatus.BLOCKED, response.getStatus());
        verify(friendRepository, times(1)).save(friend);
    }

    @Test
    void blockFriendShouldThrowRequestNotAllowedException() {
        User user3 = new User();
        user3.setId(UUID.randomUUID().toString());

        when(friendRepository.findById(friend.getId()))
                .thenReturn(Optional.ofNullable(friend));

        assertThrows(RequestNotAllowedException.class, () -> friendService.blockFriend(friend.getId(), user3));
    }

    @Test
    void getFriends() {
        List<Friend> friends = new ArrayList<>();
        friend.setStatus(FriendshipStatus.ACCEPTED);
        friends.add(friend);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Friend> page = new PageImpl<>(friends, pageable, friends.size());

        when(friendRepository.findByUserFriend_FriendAndStatus(user1, FriendshipStatus.ACCEPTED, pageable))
                .thenReturn(page);

        PageDto<Friend, FriendDto> response = friendService.getFriends(user1, pageable);

        assertEquals(page.getContent().size(), response.getContent().size());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertEquals(page.getNumber(), response.getPageNo());
        verify(friendRepository, times(1)).findByUserFriend_FriendAndStatus(user1, FriendshipStatus.ACCEPTED,pageable);
    }

    @Test
    void getBlockedUsers() {
        List<Friend> friends = new ArrayList<>();
        friend.setStatus(FriendshipStatus.BLOCKED);
        friends.add(friend);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Friend> page = new PageImpl<>(friends, pageable, friends.size());

        when(friendRepository.findByUserFriend_FriendAndStatus(user1, FriendshipStatus.BLOCKED, pageable))
                .thenReturn(page);

        PageDto<Friend, FriendDto> response = friendService.getBlockedUsers(user1, pageable);

        assertEquals(page.getContent().size(), response.getContent().size());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertEquals(page.getNumber(), response.getPageNo());
        verify(friendRepository, times(1)).findByUserFriend_FriendAndStatus(user1, FriendshipStatus.BLOCKED,pageable);
    }

    @Test
    void getPendingUsers() {
        List<Friend> friends = new ArrayList<>();
        friends.add(friend);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Friend> page = new PageImpl<>(friends, pageable, friends.size());

        when(friendRepository.findByUserFriend_FriendAndStatus(user1, FriendshipStatus.PENDING, pageable))
                .thenReturn(page);

        PageDto<Friend, FriendDto> response = friendService.getPendingUsers(user1, pageable);

        assertEquals(page.getContent().size(), response.getContent().size());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertEquals(page.getNumber(), response.getPageNo());
        verify(friendRepository, times(1)).findByUserFriend_FriendAndStatus(user1, FriendshipStatus.PENDING, pageable);
    }
}