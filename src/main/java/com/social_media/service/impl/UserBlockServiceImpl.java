package com.social_media.service.impl;

import com.social_media.entity.User;
import com.social_media.entity.UserBlock;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
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
  private static final String USER_BLOCK_ALREADY_EXISTS_MESSAGE =
      "cannot block user because a block relationship already exists";

  private static final String CANNOT_SELF_BLOCK_MESSAGE = "self block not allowed";

  private static final String USER_BLOCK_NOT_FOUND_MESSAGE = "user block not found";

  private static final String CANNOT_DELETE_USER_BLOCK_MESSAGE =
      "cannot unblock user because you are not the blocker";

  private final UserBlockRepository userBlockRepository;

  private final UserRepository userRepository;

  private final FriendRequestRepository friendRequestRepository;

  @Override
  @Transactional
  public UserBlock create(User currentUser, UUID data) {
    UUID currentUserId = currentUser.getId();

    log.info("user with id {} is blocking user with id {}", currentUserId, data);

    if (currentUser.getId().equals(data)) {
      throw new RequestNotAllowedException(CANNOT_SELF_BLOCK_MESSAGE);
    }

    if (userBlockRepository.existsBlockBetween(currentUserId, data)) {
      throw new ResourceAlreadyExistsException(USER_BLOCK_ALREADY_EXISTS_MESSAGE);
    }

    UserBlock userBlock = new UserBlock(currentUser, userRepository.getReferenceById(data));

    userBlockRepository.save(userBlock);
    friendRequestRepository.deleteByUserIdTargetUserId(currentUserId, data);

    log.info("user with id {} blocked user with id {}", currentUserId, data);

    return userBlock;
  }

  @Override
  public void delete(User user, UUID id) {
    log.info("deleting userBlock with id {}", id);

    UserBlock userBlock =
        userBlockRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(USER_BLOCK_NOT_FOUND_MESSAGE));

    if (!userBlock.getUser().getId().equals(user.getId())) {
      throw new RequestNotAllowedException(CANNOT_DELETE_USER_BLOCK_MESSAGE);
    }

    userBlockRepository.delete(userBlock);

    log.info("deleted userBlock with id {}", id);
  }

  @Override
  public Page<UserBlock> findByUserId(UUID userId, Pageable pageable) {
    log.info("fetching user blocks for user with id {}", userId);

    var page = userBlockRepository.findByUserId(userId, pageable);

    log.info("fetched user blocks for user with id {}", userId);

    return page;
  }
}
