package com.social_media.service.impl;

import com.social_media.entity.User;
import com.social_media.exception.InvalidInputException;
import com.social_media.repository.UserRepository;
import com.social_media.service.UserService;
import com.social_media.utils.CredentialsValidator;
import com.social_media.utils.EmailValidator;
import com.social_media.utils.UsernameValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.social_media.service.impl.AuthenticationServiceImpl.*;
import static com.social_media.utils.PasswordValidator.isPasswordValid;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final CredentialsValidator credentialsValidator;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void updateUser(User user, User target) {
        UUID id = user.getId();

        log.info("updating user with id {}", id);

        String username = target.getUsername().trim();
        String email = target.getEmail().trim();
        String password = target.getPassword().trim();

        credentialsValidator.validateUserCredentials(username, email, password);

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        log.info("updated user with id {}", id);
    }

    @Override
    public Page<User> searchByUsername(String username, Pageable pageable) {
        log.info("fetching users containing {} in username", username);

        Page<User> users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);

        log.info("fetched users containing {} in username", username);

        return users;
    }
}
