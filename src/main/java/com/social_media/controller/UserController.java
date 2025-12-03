package com.social_media.controller;

import com.social_media.converter.ToEntityConverter;
import com.social_media.converter.ToModelConverter;
import com.social_media.entity.User;
import com.social_media.model.PageDto;
import com.social_media.model.UserDto;
import com.social_media.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
public class UserController {
    private static final String USERNAME_SORT_PROPERTY = "username";

    private final UserService userService;

    private final ToModelConverter<User, UserDto> userToModelConverter;

    private final ToEntityConverter<User, UserDto> userDtoToEntityConverter;

    @GetMapping
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        log.info("/users with GET called, fetching current user profile");

        User user = (User) authentication.getPrincipal();
        UserDto userDto = userToModelConverter.convertToModel(user);

        log.info("fetched user profile");

        return ResponseEntity.ok(userDto);
    }

    @PutMapping
    public void updateUser(Authentication authentication, @RequestBody UserDto userDto) {
        log.info("/users with PUT called, updating current user profile");

        User user = (User) authentication.getPrincipal();
        userService.updateUser(user, userDtoToEntityConverter.convertToEntity(userDto));

        log.info("updated user profile");
    }

    @GetMapping("/{username}")
    public ResponseEntity<PageDto<User, UserDto>> searchByUsername(
            @PathVariable String username,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        log.info("/users/{} with GET called, fetching users with usernames containing the target username", username);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc(USERNAME_SORT_PROPERTY)));

        var users = new PageDto<>(
                userService.searchByUsername(username, pageable), userToModelConverter
        );

        log.info("fetching users with usernames containing the target username {}", username);

        return ResponseEntity.ok(users);
    }
}
