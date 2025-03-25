package com.social_media.services;

import com.social_media.entities.User;
import com.social_media.exceptions.InvalidProvidedInfoException;
import com.social_media.models.UserDto;
import com.social_media.repositories.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User updateUser(User user, UserDto userDto) {
        String username = userDto.username();
        if (username == null || username.isEmpty()) {
            throw new InvalidProvidedInfoException("username must be specified");
        }

        username = username.trim();
        if (username.contains(" ")) {
            throw new InvalidProvidedInfoException("username cannot contain spaces");
        }

        if (userRepository.existsByUsername(username)) {
            throw new InvalidProvidedInfoException("username is already taken, try another one");
        }

        String email = userDto.email();
        if (!userRepository.existsByEmail(email)) {
            user.setEmail(email);
        }

        user.setUsername(username);

        return userRepository.save(user);
    }

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
