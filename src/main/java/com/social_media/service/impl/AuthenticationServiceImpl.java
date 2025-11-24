package com.social_media.service.impl;

import com.social_media.config.JwtService;
import com.social_media.entity.User;
import com.social_media.exception.InvalidCredentialsException;
import com.social_media.exception.InvalidProvidedInfoException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.model.TokenDto;
import com.social_media.repository.UserRepository;
import com.social_media.service.AuthenticationService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.social_media.utils.PasswordValidator.INVALID_PASSWORD_MESSAGE;
import static com.social_media.utils.PasswordValidator.isPasswordValid;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String WRONG_USERNAME_OR_PASSWORD_MESSAGE = "wrong username or password";

    private static final String EMAIL_ALREADY_TAKEN_MESSAGE = "email is already taken";

    private static final String USERNAME_ALREADY_TAKEN_MESSAGE = "username is already taken";

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(
            UserRepository userRepository,
            JwtService jwtService,
            @Lazy PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TokenDto login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException(WRONG_USERNAME_OR_PASSWORD_MESSAGE));

        return new TokenDto(jwtService.generateToken(user.getUsername()), user.getUsername(), user.getId());
    }

    @Override
    @Transactional
    public String signup(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException(EMAIL_ALREADY_TAKEN_MESSAGE);
        }

        if (!isPasswordValid(user.getPassword())) {
            throw new InvalidProvidedInfoException(INVALID_PASSWORD_MESSAGE);
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new InvalidProvidedInfoException("name must be specified");
        }

        if (user.getLastname() == null || user.getLastname().isEmpty()) {
            throw new InvalidProvidedInfoException("lastname must be specified");
        }

        String username = user.getUsername();

        if (username == null || username.isEmpty()) {
            throw new InvalidProvidedInfoException("username must be specified");
        }

        if (userRepository.existsByUsername(username)) {
            throw new ResourceAlreadyExistsException(USERNAME_ALREADY_TAKEN_MESSAGE);
        }

        if (username.trim().contains(" ")) {
            throw new InvalidProvidedInfoException("username shouldn't contain spaces");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "signup successful";
    }
}
