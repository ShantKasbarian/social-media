package com.social_media.services;

import com.social_media.converters.UserConverter;
import com.social_media.entities.FriendRequest;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.exceptions.InvalidProvidedInfoException;
import com.social_media.exceptions.RequestNotAllowedException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.exceptions.ResourceNotFoundException;
import com.social_media.models.FriendRequestDto;
import com.social_media.models.PageDto;
import com.social_media.models.UserDto;
import com.social_media.repositories.FriendRequestRepository;
import com.social_media.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserConverter userConverter;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("someone@example.com");
        user.setPassword("Password123+");
        user.setUsername("johnDoe");
        user.setName("John");
        user.setLastname("Doe");
        user.setBlockedUsers(new ArrayList<>());

        userDto = new UserDto(
                user.getId(),
                "someone1@example.com",
                user.getPassword(),
                "someUsername",
                user.getName(),
                user.getLastname()
        );
    }

    @Test
    void searchByUsername() {
        User user2 = new User();
        user2.setUsername("Jack");

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);

        when(userRepository.findByUsernameContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(users));

        PageDto<User, UserDto> response = userService.searchByUsername("J", PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.asc("username"))
        ));

        assertEquals(users.size(), response.getContent().size());
        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase("J", PageRequest.of(
                0,
                10,
                Sort.by(Sort.Order.asc("username"))
        ));
    }

    @Test
    void updateUsername() {
        String oldUsername = user.getUsername();

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User response = userService.updateUsername(user, userDto.username());

        assertEquals(userDto.username(), response.getUsername());
        assertEquals(user.getUsername(), response.getUsername());
        assertNotEquals(oldUsername, response.getUsername());
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateEmail() {
        String oldEmail = user.getEmail();

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User response = userService.updateEmail(user, userDto.email());

        assertNotEquals(oldEmail, response.getEmail());
        assertEquals(userDto.email(), response.getEmail());
        assertEquals(user.getEmail(), response.getEmail());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUsernameShouldThrowInvalidProvidedInfoExceptionWhenUsernameIsNull() {
        assertThrows(InvalidProvidedInfoException.class, () -> userService.updateUsername(user, null));
    }

    @Test
    void updateUsernameShouldThrowInvalidProvidedInfoExceptionWhenUsernameIsEmpty() {
        assertThrows(InvalidProvidedInfoException.class, () -> userService.updateUsername(user, ""));
    }

    @Test
    void updateUsernameShouldThrowInvalidProvidedInfoExceptionWhenUsernameContainsSpace() {
        userDto = new UserDto(
                user.getId(),
                "someone1@example.com",
                user.getPassword(),
                "John Doe",
                user.getName(),
                user.getLastname()
        );

        assertThrows(InvalidProvidedInfoException.class, () -> userService.updateUsername(user, userDto.username()));
    }

    @Test
    void updateEmailShouldThrowResourceAlreadyExistsExceptionWhenAnotherUserWithProvidedEmailExists() {
        when(userRepository.existsByEmail(userDto.email())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateEmail(user, userDto.email()));
    }

    @Test
    void updateUserShouldThrowResourceAlreadyExistsExceptionWhenAnotherUserWithProvidedUsernameExists() {
        when(userRepository.existsByUsername(userDto.username())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUsername(user, userDto.username()));
    }

    @Test
    void updatePassword() {
        String oldPassword = user.getPassword();
        String newPassword = "Password12+";

        when(userRepository.save(user)).thenReturn(user);

        String response = userService.updatePassword(user, newPassword);

        assertEquals("password has been changed", response);
        assertFalse(bCryptPasswordEncoder.matches(oldPassword, user.getPassword()));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updatePasswordShouldThrowInvalidProvidedInfoExceptionWhenPasswordIsNull() {
        assertThrows(InvalidProvidedInfoException.class, () -> userService.updatePassword(user, null));
    }

    @Test
    void updatePasswordShouldThrowInvalidProvidedInfoExceptionWhenPasswordIsEmpty() {
        assertThrows(InvalidProvidedInfoException.class, () -> userService.updatePassword(user, ""));
    }

    @Test
    void updatePasswordShouldThrowInvalidProvidedInfoExceptionWhenPasswordIsInvalid() {
        assertThrows(InvalidProvidedInfoException.class, () -> userService.updatePassword(user, "Password123"));
    }

    @Test
    void blockUser() {
        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());

        String expected = "user has been blocked";

        FriendRequest friendRequest = new FriendRequest(
            UUID.randomUUID().toString(),
            user,
            user2,
            FriendshipStatus.ACCEPTED
        );

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(userRepository.save(user)).thenReturn(user);
        when(friendRequestRepository.findByUser_idFriend_id(user.getId(), user2.getId()))
                .thenReturn(Optional.ofNullable(friendRequest));


        String response = userService.blockUser(user2.getId(), user);

        assertNotNull(response);
        assertEquals(expected, response);
        assertEquals(FriendshipStatus.BLOCKED, friendRequest.getStatus());
        assertEquals(user.getBlockedUsers().getFirst().getId(), user2.getId());
        verify(friendRequestRepository, times(1)).save(friendRequest);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void blockFriendShouldThrowResourceAlreadyExistsException() {
        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setBlockedUsers(new ArrayList<>());

        user.getBlockedUsers().add(user2);

        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.blockUser(user2.getId(), user));
    }

    @Test
    void blockFriendShouldThrowResourceNotFoundExceptionWhenTargetUserIsNotFound() {
        String id = "some random id";

        when(userRepository.findById(id))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> userService.blockUser(id, user));
    }

    @Test
    void unblockUser() {
        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setBlockedUsers(new ArrayList<>());

        user.getBlockedUsers().add(user2);

        FriendRequest friendRequest = new FriendRequest(
                UUID.randomUUID().toString(),
                user,
                user2,
                FriendshipStatus.BLOCKED
        );

        when(userRepository.findById(anyString())).thenReturn(Optional.ofNullable(user2));
        when(userRepository.save(user)).thenReturn(user);
        when(friendRequestRepository.findByUser_idFriend_id(user.getId(), user2.getId()))
                .thenReturn(Optional.of(friendRequest));
        when(friendRequestRepository.save(friendRequest)).thenReturn(friendRequest);

        String response = userService.unblockUser(user2.getId(), user);

        assertNotNull(response);
        assertEquals("user has been unblocked", response);
        assertEquals(FriendshipStatus.PENDING, friendRequest.getStatus());
        verify(friendRequestRepository, times(1)).save(friendRequest);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void unblockUserShouldThrowResourceNotFoundExceptionWhenTargetUserIsNotFound() {
        when(friendRequestRepository.findById(anyString()))
                .thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> userService.unblockUser("some id", user));
    }

    @Test
    void getBlockedUsers() {
        user.getBlockedUsers().add(new User());
        user.getBlockedUsers().add(new User());

        PageDto<User, UserDto> response = userService.getBlockedUsers(user, PageRequest.of(0, 10));

        assertEquals(user.getBlockedUsers().size(), response.getContent().size());
    }
}