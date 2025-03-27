package com.social_media.services;

import com.social_media.entities.User;
import com.social_media.exceptions.InvalidProvidedInfoException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.models.UserDto;
import com.social_media.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
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

        when(userRepository.findByUsernameContainingIgnoreCase("J")).thenReturn(users);

        List<User> response = userService.searchByUsername("J");

        assertEquals(users.size(), response.size());
        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase("J");
    }

    @Test
    void updateUser() {
        String oldEmail = user.getEmail();
        String oldUsername = user.getUsername();

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User response = userService.updateUser(user, userDto);

        assertEquals(userDto.username(), response.getUsername());
        assertEquals(user.getUsername(), response.getUsername());
        assertNotEquals(oldEmail, response.getEmail());
        assertNotEquals(oldUsername, response.getUsername());
        assertEquals(userDto.email(), response.getEmail());
        assertEquals(user.getEmail(), response.getEmail());
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserShouldThrowInvalidProvidedInfoExceptionWhenUsernameIsNull() {
        userDto = new UserDto(
                user.getId(),
                "someone1@example.com",
                user.getPassword(),
                null,
                user.getName(),
                user.getLastname()
        );

        assertThrows(InvalidProvidedInfoException.class, () -> userService.updateUser(user, userDto));
    }

    @Test
    void updateUserShouldThrowInvalidProvidedInfoExceptionWhenUsernameIsEmpty() {
        userDto = new UserDto(
                user.getId(),
                "someone1@example.com",
                user.getPassword(),
                "",
                user.getName(),
                user.getLastname()
        );

        assertThrows(InvalidProvidedInfoException.class, () -> userService.updateUser(user, userDto));
    }

    @Test
    void updateUserShouldThrowInvalidProvidedInfoExceptionWhenUsernameContainsSpace() {
        userDto = new UserDto(
                user.getId(),
                "someone1@example.com",
                user.getPassword(),
                "John Doe",
                user.getName(),
                user.getLastname()
        );

        assertThrows(InvalidProvidedInfoException.class, () -> userService.updateUser(user, userDto));
    }

    @Test
    void updateUserShouldThrowResourceAlreadyExistsExceptionWhenAnotherUserWithProvidedEmailExists() {
        when(userRepository.existsByUsername(userDto.username())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.email())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(user, userDto));
    }

    @Test
    void updateUserShouldThrowResourceAlreadyExistsExceptionWhenAnotherUserWithProvidedUsernameExists() {
        when(userRepository.existsByUsername(userDto.username())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(user, userDto));
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
}