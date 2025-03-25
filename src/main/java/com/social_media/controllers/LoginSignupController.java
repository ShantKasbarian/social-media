package com.social_media.controllers;

import com.social_media.converters.UserConverter;
import com.social_media.models.UserDto;
import com.social_media.services.LoginSignupService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginSignupController {
    private final LoginSignupService loginSignupService;

    private final UserConverter userConverter;

    public LoginSignupController(LoginSignupService loginSignupService, UserConverter userConverter) {
        this.loginSignupService = loginSignupService;
        this.userConverter = userConverter;
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDto userDto) {
        return loginSignupService.login(userDto.email(), userDto.password());
    }

    @PostMapping("/signup")
    public String signup(@RequestBody UserDto userDto) {
        return loginSignupService.signup(userConverter.convertToEntity(userDto));
    }
}
