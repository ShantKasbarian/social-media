package com.social_media.converter;

import com.social_media.entity.User;
import com.social_media.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserConverterTest {
    @InjectMocks
    private UserConverter userConverter;

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
    void convertToEntity() {
        User user = userConverter.convertToEntity(userDto);

        assertNotNull(user);
        assertEquals(userDto.id(), user.getId());
        assertEquals(userDto.email(), user.getEmail());
        assertEquals(userDto.username(), user.getUsername());
        assertEquals(userDto.password(), user.getPassword());
        assertEquals(userDto.firstname(), user.getFirstname());
        assertEquals(userDto.lastname(), user.getLastname());
    }

    @Test
    void convertToModel() {
        UserDto userDto = userConverter.convertToModel(user);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.id());
        assertEquals(user.getEmail(), userDto.email());
        assertEquals(user.getUsername(), userDto.username());
        assertEquals(user.getPassword(), userDto.password());
        assertEquals(user.getFirstname(), userDto.firstname());
        assertEquals(user.getLastname(), userDto.lastname());
    }
}
