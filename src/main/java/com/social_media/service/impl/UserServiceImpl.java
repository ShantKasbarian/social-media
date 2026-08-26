package com.social_media.service.impl;

import com.social_media.entity.User;
import com.social_media.model.UserPatchDto;
import com.social_media.repository.UserRepository;
import com.social_media.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @Override
  public User update(User user, UserPatchDto userPatchDto) {
    UUID id = user.getId();

    log.info("updating user with id {}", id);

    user.setUsername(userPatchDto.username().trim());
    user.setEmail(userPatchDto.email().trim());
    user.setPassword(passwordEncoder.encode(userPatchDto.password().trim()));

    log.info("updated user with id {}", id);

    return user;
  }

  @Override
  public Page<User> findByUsername(String username, Pageable pageable) {
    log.info("fetching users containing {} in username", username);

    Page<User> users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);

    log.info("fetched users containing {} in username", username);

    return users;
  }
}
