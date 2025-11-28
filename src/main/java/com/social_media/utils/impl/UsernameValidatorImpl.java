package com.social_media.utils.impl;

import com.social_media.repository.UserRepository;
import com.social_media.utils.UsernameValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UsernameValidatorImpl implements UsernameValidator {
    private final UserRepository userRepository;

    @Override
    public boolean isUsernameValid(String username) {
        if (username == null) {
            return false;
        }

        return !username.contains(" ") && !userRepository.existsByUsername(username);
    }
}
