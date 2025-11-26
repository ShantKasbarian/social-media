package com.social_media.service.impl;

import com.social_media.entity.User;
import com.social_media.exception.InvalidProvidedInfoException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.repository.UserRepository;
import com.social_media.service.UserService;
import com.social_media.utils.EmailValidator;
import com.social_media.utils.UsernameValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.social_media.service.impl.AuthenticationServiceImpl.*;
import static com.social_media.utils.PasswordValidator.isPasswordValid;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UsernameValidator usernameValidator;

    private final EmailValidator emailValidator;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<User> searchByUsername(String username, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }

    @Override
    public void updateUser(User user, User target) {
        String username = target.getUsername().trim();
        String email = target.getEmail().trim();
        String password = target.getPassword().trim();

        if (!usernameValidator.isUsernameValid(username)) {
            throw new InvalidProvidedInfoException(INVALID_USERNAME_MESSAGE);
        }

        if (!emailValidator.isEmailValid(email)) {
            throw new ResourceAlreadyExistsException(INVALID_EMAIL_MESSAGE);
        }

        if (!isPasswordValid(password)) {
            throw new InvalidProvidedInfoException(INVALID_PASSWORD_MESSAGE);
        }

        user.setFirstname(target.getFirstname());
        user.setLastname(target.getLastname());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }
}
