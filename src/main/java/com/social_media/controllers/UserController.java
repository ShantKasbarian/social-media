package com.social_media.controllers;

import com.social_media.converters.UserConverter;
import com.social_media.entities.FriendRequest;
import com.social_media.entities.User;
import com.social_media.models.FriendRequestDto;
import com.social_media.models.PageDto;
import com.social_media.models.ResponseDto;
import com.social_media.models.UserDto;
import com.social_media.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    private final UserConverter userConverter;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(
                userConverter.convertToModel((User) authentication.getPrincipal())
        );
    }

    @PutMapping("/update/username")
    public ResponseEntity<UserDto> updateUsername(@RequestBody UserDto userDto, Authentication authentication) {
        return ResponseEntity.ok(
                userConverter.convertToModel(
                    userService.updateUsername((User) authentication.getPrincipal(),
                            userDto.username()
                    )
                )
        );
    }

    @PutMapping("/update/email")
    public ResponseEntity<UserDto> updateEmail(@RequestBody UserDto userDto, Authentication authentication) {
        return ResponseEntity.ok(
                userConverter.convertToModel(
                        userService.updateEmail((User) authentication.getPrincipal(),
                                userDto.email()
                        )
                )
        );
    }

    @PutMapping("/password")
    public ResponseEntity<ResponseDto> updatePassword(@RequestBody UserDto userDto, Authentication authentication) {
        return ResponseEntity.ok(
                new ResponseDto(
                    userService.updatePassword(
                            (User) authentication.getPrincipal(),
                            userDto.password()
                    )
                )
        );
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

    @PutMapping("/{userId}/block")
    public ResponseEntity<ResponseDto> blockUser(Authentication authentication, @PathVariable String userId) {
        return ResponseEntity.ok(
                new ResponseDto(userService.blockUser(userId, (User) authentication.getPrincipal()))
        );
    }

    @PutMapping("/{userId}/unblock")
    public ResponseEntity<ResponseDto> unblockUser(Authentication authentication, @PathVariable String userId) {
        return ResponseEntity.ok(
                new ResponseDto(userService.unblockUser(userId, (User) authentication.getPrincipal()))
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
