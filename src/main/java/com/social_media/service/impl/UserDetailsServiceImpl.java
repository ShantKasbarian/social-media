package com.social_media.service.impl;

import com.social_media.entity.User;
import com.social_media.exception.InvalidCredentialsException;
import com.social_media.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final String INVALID_CREDENTIALS_MESSAGE = "wrong username or password";

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("fetching user with username {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        log.info("fetched user with username {}", username);

        return user;
    }
}
