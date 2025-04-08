package com.social_media.services;

import com.social_media.entities.User;
import com.social_media.exceptions.InvalidProvidedInfoException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.models.UserDto;
import com.social_media.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(
            UserRepository userRepository,
            @Lazy
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<User> searchByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    @Transactional
    public User updateUsername(User user, String username) {
        if (username == null || username.isEmpty()) {
            throw new InvalidProvidedInfoException("username must be specified");
        }

        username = username.trim();
        if (username.contains(" ")) {
            throw new InvalidProvidedInfoException("username cannot contain spaces");
        }

        if (userRepository.existsByUsername(username) && !user.getUsername().equals(username)) {
            throw new ResourceAlreadyExistsException("username is already taken, try another one");
        }

        user.setUsername(username);

        return userRepository.save(user);
    }

    public User updateEmail(User user, @Email String email) {
        if (userRepository.existsByEmail(email) && !user.getEmail().equals(email)) {
            throw new ResourceAlreadyExistsException("email already in use");
        }

        user.setEmail(email);
        return userRepository.save(user);
    }

    @Transactional
    public String updatePassword(
            User user,
            String password
    ) {
        if (password == null || password.isEmpty() || !LoginSignupService.isPasswordValid(password)) {
            throw new InvalidProvidedInfoException("invalid password");
        }

        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return "password has been changed";
    }
}
