package com.social_media.utils.impl;

import com.social_media.repository.UserRepository;
import com.social_media.utils.EmailValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class EmailValidatorImpl implements EmailValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$\n";

    private final UserRepository userRepository;

    @Override
    public boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);

        return matcher.find() && userRepository.existsByEmail(email);
    }
}
