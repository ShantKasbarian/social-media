package com.social_media.controllers;

import com.social_media.converters.UserConverter;
import com.social_media.entities.User;
import com.social_media.models.ResponseDto;
import com.social_media.models.UserDto;
import com.social_media.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<UserDto>> searchByUsername(@PathVariable String username) {
        return ResponseEntity.ok(
                userService.searchByUsername(username)
                    .stream()
                    .map(userConverter::convertToModel)
                    .toList()
        );
    }
}
