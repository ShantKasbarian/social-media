package com.social_media.controller;

import com.social_media.converter.FriendRequestConverter;
import com.social_media.converter.UserConverter;
import com.social_media.entity.User;
import com.social_media.model.FriendRequestDto;
import com.social_media.model.PageDto;
import com.social_media.model.UserDto;
import com.social_media.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    private final UserConverter userConverter;

    private final FriendRequestConverter friendRequestConverter;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(
                userConverter.convertToModel((User) authentication.getPrincipal())
        );
    }

    @PutMapping("/update/username")
    public void updateUsername(@RequestBody UserDto userDto, Authentication authentication) {
        userService.updateUsername((User) authentication.getPrincipal(), userDto.username());
    }

    @PutMapping("/update/email")
    public void updateEmail(@RequestBody UserDto userDto, Authentication authentication) {
        userService.updateEmail((User) authentication.getPrincipal(), userDto.email());
    }

    @PutMapping("/password")
    public void updatePassword(@RequestBody UserDto userDto, Authentication authentication) {
        userService.updatePassword((User) authentication.getPrincipal(), userDto.password());
    }

    @GetMapping("/{username}/search")
    public ResponseEntity<PageDto<User, UserDto>> searchByUsername(
            @PathVariable String username,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                userService.searchByUsername(
                        username,
                        PageRequest.of(
                                page,
                                size,
                                Sort.by(Sort.Order.asc("username"))
                        )
                )
        );
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<FriendRequestDto> blockUser(Authentication authentication, @PathVariable String userId) {
        return ResponseEntity.ok(
                friendRequestConverter.convertToModel(
                        userService.blockUser(userId, (User) authentication.getPrincipal())
                )
        );
    }

    @PatchMapping("/{userId}/unblock")
    public ResponseEntity<FriendRequestDto> unblockUser(Authentication authentication, @PathVariable String userId) {
        return ResponseEntity.ok(
                friendRequestConverter.convertToModel(
                        userService.unblockUser(userId, (User) authentication.getPrincipal())
                )
        );
    }

    @GetMapping("/blocked")
    public ResponseEntity<PageDto<User, UserDto>> getBlockedUsers(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                userService.getBlockedUsers(
                        (User) authentication.getPrincipal(),
                        PageRequest.of(page, size)
                )
        );
    }
}
