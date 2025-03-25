package com.social_media.controllers;

import com.social_media.converters.UserConverter;
import com.social_media.entities.User;
import com.social_media.models.UserDto;
import com.social_media.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    private final UserConverter userConverter;

    public UserController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(
                userConverter.convertToModel((User) authentication.getPrincipal())
        );
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, Authentication authentication) {
        return ResponseEntity.ok(
                userConverter.convertToModel(
                    userService.updateUser((User) authentication.getPrincipal(),
                            userDto
                    )
                )
        );
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody UserDto userDto, Authentication authentication) {
        return ResponseEntity.ok(
                userService.updatePassword(
                        (User) authentication.getPrincipal(),
                        userDto.password()
                )
        );
    }
}
