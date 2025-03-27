package com.social_media.services;

import com.social_media.config.JwtService;
import com.social_media.entities.User;
import com.social_media.exceptions.InvalidCredentialsException;
import com.social_media.exceptions.InvalidProvidedInfoException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.repositories.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LoginSignupService implements UserDetailsService {
    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder;

    public LoginSignupService(
            UserRepository userRepository,
            JwtService jwtService,
            @Lazy
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String email, String password) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("wrong email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("wrong email or password");
        }

        return jwtService.generateToken(user.getUsername());
    }

    public String signup(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException("email already in use");
        }

        if (!isPasswordValid(user.getPassword())) {
            throw new InvalidProvidedInfoException("invalid password");
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
            throw new ResourceAlreadyExistsException("username already exist try a different one");
        }

        if (username.trim().contains(" ")) {
            throw new InvalidProvidedInfoException("username shouldn't contain spaces");
        }

        user.setId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "signup successful";
    }

    public static boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            throw new InvalidCredentialsException("password must be at least 6 characters long");
        }

        Pattern numberPattern = Pattern.compile("[0-9]");
        Pattern uppercasePattern = Pattern.compile("[A-Z]");
        Pattern lowercasePattern = Pattern.compile("[a-z]");
        Pattern specialCharacterPattern = Pattern.compile(".*[!@#$%^&*(),.?\":{}|<>+].*");

        Matcher number = numberPattern.matcher(password);
        Matcher uppercase = uppercasePattern.matcher(password);
        Matcher lowercase = lowercasePattern.matcher(password);
        Matcher specialCharacter = specialCharacterPattern.matcher(password);

        return number.find() && uppercase.find() && lowercase.find() && specialCharacter.find();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("wrong email or password"));
    }
}
