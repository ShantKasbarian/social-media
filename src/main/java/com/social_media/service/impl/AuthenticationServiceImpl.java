package com.social_media.service.impl;

import com.social_media.config.JwtService;
import com.social_media.entity.User;
import com.social_media.exception.InvalidCredentialsException;
import com.social_media.exception.InvalidInputException;
import com.social_media.model.TokenDto;
import com.social_media.repository.UserRepository;
import com.social_media.service.AuthenticationService;
import com.social_media.utils.EmailValidator;
import com.social_media.utils.UsernameValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.social_media.utils.PasswordValidator.isPasswordValid;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String WRONG_USERNAME_OR_PASSWORD_MESSAGE = "wrong username or password";

    public static final String INVALID_EMAIL_MESSAGE = "email is already taken";

    public static final String INVALID_USERNAME_MESSAGE = "username is invalid";

    public static final String INVALID_PASSWORD_MESSAGE = "password must be at least 6 characters long, contain at least 1 uppercase, 1 lowercase, 1 number and 1 special character";

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final UsernameValidator usernameValidator;

    private final EmailValidator emailValidator;

    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenDto login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException(WRONG_USERNAME_OR_PASSWORD_MESSAGE));

        return new TokenDto(jwtService.generateToken(user.getUsername()), user.getUsername(), user.getId());
    }

    @Override
    @Transactional
    public TokenDto signup(User user) {
        String username = user.getUsername().trim();

        if (!usernameValidator.isUsernameValid(username)) {
            throw new InvalidInputException(INVALID_USERNAME_MESSAGE);
        }

        if (!emailValidator.isEmailValid(user.getEmail())) {
            throw new InvalidInputException(INVALID_EMAIL_MESSAGE);
        }

        if (!isPasswordValid(user.getPassword())) {
            throw new InvalidInputException(INVALID_PASSWORD_MESSAGE);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return new TokenDto(jwtService.generateToken(user.getUsername()), user.getUsername(), user.getId());
    }
}
