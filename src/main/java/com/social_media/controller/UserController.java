package com.social_media.controller;

import com.social_media.converter.ToEntityConverter;
import com.social_media.converter.ToModelConverter;
import com.social_media.entity.User;
import com.social_media.model.PageDto;
import com.social_media.model.UserDto;
import com.social_media.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private static final String USERNAME_SORT_PROPERTY = "username";

    private final UserService userService;

    private final ToModelConverter<User, UserDto> userToModelConverter;

    private final ToEntityConverter<User, UserDto> userDtoToEntityConverter;

    @GetMapping
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        var user = userToModelConverter.convertToModel((User) authentication.getPrincipal());

        return ResponseEntity.ok(user);
    }

    @PutMapping
    public void updateUser(Authentication authentication, @RequestBody UserDto userDto) {
        User user = (User) authentication.getPrincipal();
        userService.updateUser(user, userDtoToEntityConverter.convertToEntity(userDto));
    }

    @GetMapping("/{username}")
    public ResponseEntity<PageDto<User, UserDto>> searchByUsername(
            @PathVariable String username,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc(USERNAME_SORT_PROPERTY)));

        var users = new PageDto<>(
                userService.searchByUsername(username, pageable), userToModelConverter
        );

        return ResponseEntity.ok(users);
    }
}
