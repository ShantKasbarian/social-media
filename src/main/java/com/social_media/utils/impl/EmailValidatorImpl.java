package com.social_media.utils.impl;

import com.social_media.repository.UserRepository;
import com.social_media.utils.EmailValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@AllArgsConstructor
public class EmailValidatorImpl implements EmailValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private final UserRepository userRepository;

    @Override
    public boolean isEmailValid(String email) {
        log.info("validating email {}", email);

        if (email == null) {
            return false;
        }

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);

        boolean isValid = matcher.find() && !userRepository.existsByEmail(email);

        log.info("validated email {}", email);

        return isValid;
    }
}
