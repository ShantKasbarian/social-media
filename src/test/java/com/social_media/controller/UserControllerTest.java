package com.social_media.controller;

import com.social_media.converter.UserConverter;
import com.social_media.entity.User;
import com.social_media.model.PageDto;
import com.social_media.model.ErrorDto;
import com.social_media.model.UserDto;
import com.social_media.service.UserService;
import com.social_media.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserConverter userConverter;

    @Mock
    private Authentication authentication;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("someone@example.com");
        user.setPassword("Password123+");
        user.setUsername("johnDoe");
        user.setFirstname("John");
        user.setLastname("Doe");

        userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname()
        );
    }

    @Test
    void getProfile() {
        when(authentication.getPrincipal()).thenReturn(user);
        when(userConverter.convertToModel(any(User.class))).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.getProfile(authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userDto.id(), response.getBody().id());
    }

    @Test
    void updateUser() {
        when(authentication.getPrincipal()).thenReturn(user);
        doNothing().when(userService).updateUser(any(User.class), any(User.class));
        when(userConverter.convertToEntity(any(UserDto.class))).thenReturn(user);

        userController.updateUser(authentication, userDto);

        verify(authentication).getPrincipal();
        verify(userService).updateUser(any(User.class), any(User.class));
        verify(userConverter).convertToEntity(any(UserDto.class));
    }

    @Test
    void searchByUsername() {
        List<User> users = new ArrayList<>();
        users.add(user);

        Page<User> page = new PageImpl<>(users);

        when(userService.searchByUsername(anyString(), any(Pageable.class)))
                .thenReturn(page);

        var response = userController.searchByUsername("o", 0, 10);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(users.size(), response.getBody().getContent().size());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).searchByUsername(anyString(), any(Pageable.class));
    }
}
