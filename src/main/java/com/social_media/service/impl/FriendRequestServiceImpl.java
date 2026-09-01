package com.social_media.service.impl;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.UserBlockRepository;
import com.social_media.repository.UserRepository;
import com.social_media.service.FriendRequestService;
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
public class FriendRequestServiceImpl implements FriendRequestService {
  private static final String USER_NOT_FOUND_MESSAGE = "user not found";

  private static final String FRIEND_REQUEST_ALREADY_SENT_MESSAGE = "friend request already exists";

  private static final String UNABLE_TO_UPDATE_FRIEND_REQUEST_STATUS_MESSAGE =
      "cannot update friendRequest status";

  private static final String UNABLE_TO_DELETE_FRIEND_REQUEST_MESSAGE =
      "cannot delete friend request";

  private static final String FRIEND_REQUEST_NOT_FOUND_MESSAGE = "friend request not found";

  private final FriendRequestRepository friendRequestRepository;

  private final UserBlockRepository userBlockRepository;

  private final UserRepository userRepository;

  @Override
  @Transactional
  public FriendRequest create(User user, UUID data) {
    UUID currentUserId = user.getId();

    log.info("creating friend request with userId {} and data {}", currentUserId, data);

    User targetUser =
        userRepository
            .findById(data)
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

    if (friendRequestRepository.existsByUserIdTargetUserId(currentUserId, data)
        || userBlockRepository.existsBlockBetween(currentUserId, data)) {
      throw new ResourceAlreadyExistsException(FRIEND_REQUEST_ALREADY_SENT_MESSAGE);
    }

    FriendRequest friendRequest = new FriendRequest(user, targetUser, FriendRequest.Status.PENDING);

    friendRequestRepository.save(friendRequest);

    log.info("created friend request with userId {} and data {}", currentUserId, data);

    return friendRequest;
  }

  @Override
  @Transactional
  public FriendRequest acceptFriendRequest(User user, UUID requestId) {
    log.info("updating friendRequest with id {}", requestId);

    FriendRequest friendRequest =
        friendRequestRepository
            .findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException(FRIEND_REQUEST_NOT_FOUND_MESSAGE));

    if (FriendRequest.Status.PENDING.equals(friendRequest.getStatus())
        && !user.getId().equals(friendRequest.getTargetUser().getId())) {
      throw new RequestNotAllowedException(UNABLE_TO_UPDATE_FRIEND_REQUEST_STATUS_MESSAGE);
    }

    friendRequest.setStatus(FriendRequest.Status.ACCEPTED);

    log.info("updated friendRequest with id {}", requestId);

    return friendRequest;
  }

  @Override
  @Transactional
  public void delete(User user, UUID id) {
    log.info("deleting friendRequest with id {}", id);

    FriendRequest friendRequest =
        friendRequestRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(FRIEND_REQUEST_NOT_FOUND_MESSAGE));

    UUID userId = user.getId();

    if (!friendRequest.getUser().getId().equals(userId)
        && !friendRequest.getTargetUser().getId().equals(userId)) {
      throw new RequestNotAllowedException(UNABLE_TO_DELETE_FRIEND_REQUEST_MESSAGE);
    }

    friendRequestRepository.delete(friendRequest);

    log.info("deleted friendRequest with id {}", id);
  }

  @Override
  public Page<FriendRequest> findByUserAndStatus(
      User user, FriendRequest.Status status, Pageable pageable) {
    UUID id = user.getId();

    log.info("fetching friendRequests of user with user {} and status {}", id, status);

    Page<FriendRequest> friendRequests =
        friendRequestRepository.findByUserStatus(user, status, pageable);

    log.info("fetched friendRequests of user with user {} and status {}", id, status);

    return friendRequests;
  }
}
