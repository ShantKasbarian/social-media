package com.social_media.service.impl;

import com.social_media.entity.User;
import com.social_media.entity.UserBlock;
import com.social_media.repository.UserBlockRepository;
import com.social_media.repository.UserRepository;
import com.social_media.service.UserBlockService;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserBlockServiceImpl implements UserBlockService {
  private final UserBlockRepository userBlockRepository;

  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserBlock create(User currentUser, UUID targetUserId) {
    UUID currentUserId = currentUser.getId();

    log.info("user with id {} is blocking user with id {}", currentUserId, targetUserId);

    UserBlock userBlock = new UserBlock();
    userBlock.setUser(currentUser);
    userBlock.setTargetUser(userRepository.getReferenceById(targetUserId));

    userBlockRepository.save(userBlock);

    log.info("user with id {} blocked user with id {}", currentUserId, targetUserId);

    return userBlock;
  }

  @Override
  public Page<UserBlock> getUserBlocksByUserId(UUID userId, Pageable pageable) {
    log.info("fetching user blocks for user with id {}", userId);

    var page = userBlockRepository.findByUserId(userId, pageable);

    log.info("fetched user blocks for user with id {}", userId);

    return page;
  }
}
