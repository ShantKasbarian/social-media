package com.social_media.utils.impl;

import com.social_media.repository.UserRepository;
import com.social_media.utils.UsernameValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class UsernameValidatorImpl implements UsernameValidator {
    private final UserRepository userRepository;

    @Override
    public boolean isUsernameValid(String username) {
        log.info("validating username {}", username);

        if (username == null) {
            return false;
        }

        boolean isValid = !username.contains(" ") && !userRepository.existsByUsername(username);

        log.info("validated username {}", username);

        return isValid;
    }
}
