package com.social_media.service.impl;

import com.social_media.entity.User;
import com.social_media.exception.InvalidProvidedInfoException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.repository.UserRepository;
import com.social_media.service.UserService;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.social_media.utils.PasswordValidator.INVALID_PASSWORD_MESSAGE;
import static com.social_media.utils.PasswordValidator.isPasswordValid;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String USERNAME_CANNOT_CONTAIN_SPACES_MESSAGE = "username cannot contain spaces";

    private static final String USERNAME_ALREADY_TAKEN_MESSAGE = "username is already taken";

    private static final String EMAIL_ALREADY_TAKEN_MESSAGE = "email is already taken";

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<User> searchByUsername(String username, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }

    @Override
    public void updateUser(User user, User target) {
        String username = target.getUsername();
        String email = target.getEmail();
        String password = target.getPassword();

        validateUsername(user, username);
        validateEmail(user, email);

        if (password == null || password.isEmpty() || !isPasswordValid(password)) {
            throw new InvalidProvidedInfoException(INVALID_PASSWORD_MESSAGE);
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    private void validateUsername(User user, String username) {
        if (username == null || username.isEmpty()) {
            throw new InvalidProvidedInfoException("username must be specified");
        }

        username = username.trim();
        if (username.contains(" ")) {
            throw new InvalidProvidedInfoException(USERNAME_CANNOT_CONTAIN_SPACES_MESSAGE);
        }

        if (userRepository.existsByUsername(username) && !user.getUsername().equals(username)) {
            throw new ResourceAlreadyExistsException(USERNAME_ALREADY_TAKEN_MESSAGE);
        }

        user.setUsername(username);
    }

    private void validateEmail(User user, @Email String email) {
        if (email == null|| email.isEmpty()) {
            throw new InvalidProvidedInfoException("email cannot be empty");
        }

        if (userRepository.existsByEmail(email) && !user.getEmail().equals(email)) {
            throw new ResourceAlreadyExistsException(EMAIL_ALREADY_TAKEN_MESSAGE);
        }
    }
}
