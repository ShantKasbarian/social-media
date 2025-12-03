package com.social_media.service.impl;

import com.social_media.annotation.ValidateUserNotBlocked;
import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.UserRepository;
import com.social_media.service.FriendRequestService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
    private static final String USER_NOT_FOUND_MESSAGE = "user not found";

    private static final String FRIEND_REQUEST_ALREADY_SENT_MESSAGE = "you have already sent a friend request";

    private static final String FRIEND_REQUEST_PENDING_MESSAGE = "cannot accept friend request of the target user";

    private static final String FRIEND_REQUEST_NOT_FOUND_MESSAGE = "friend request not found";

    private static final String UNABLE_TO_DELETE_FRIEND_REQUEST = "cannot delete friend request";

    private final FriendRequestRepository friendRequestRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public FriendRequest createFriendRequest(User user, UUID targetUserId) {
        UUID currentUserId = user.getId();

        log.info("creating friend request with userId {} and targetUserId {}", currentUserId, targetUserId);

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));


        if (friendRequestRepository.existsByUserIdTargetUserId(currentUserId, targetUserId)) {
            throw new ResourceAlreadyExistsException(FRIEND_REQUEST_ALREADY_SENT_MESSAGE);
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUser(user);
        friendRequest.setTargetUser(targetUser);
        friendRequest.setStatus(FriendRequest.Status.PENDING);

        friendRequestRepository.save(friendRequest);

        log.info("created friend request with userId {} and targetUserId {}", currentUserId, targetUserId);

        return friendRequest;
    }

    @Override
    @Transactional
    @ValidateUserNotBlocked
    public FriendRequest updateFriendRequestStatus(User user, UUID requestId, FriendRequest.Status status) {
        log.info("updating friendRequest with id {}", requestId);

        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(FRIEND_REQUEST_NOT_FOUND_MESSAGE));

        if (
                !user.getId().equals(friendRequest.getTargetUser().getId()) &&
                FriendRequest.Status.ACCEPTED.equals(status)
        ) {
            throw new RequestNotAllowedException(FRIEND_REQUEST_PENDING_MESSAGE);
        }

        friendRequest.setStatus(status);

        friendRequestRepository.save(friendRequest);

        log.info("updated friendRequest with id {}", requestId);

        return friendRequest;
    }

    @Override
    @Transactional
    @ValidateUserNotBlocked
    public void deleteFriendRequest(User user, UUID requestId) {
        log.info("deleting friendRequest with id {}", requestId);

        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(FRIEND_REQUEST_NOT_FOUND_MESSAGE));

        UUID userId = user.getId();
        UUID targetUserId = friendRequest.getTargetUser().getId();

        if (
                !friendRequest.getUser().getId().equals(userId) &&
                !targetUserId.equals(userId)
        ) {
            throw new RequestNotAllowedException(UNABLE_TO_DELETE_FRIEND_REQUEST);
        }

        friendRequestRepository.delete(friendRequest);

        log.info("deleted friendRequest with id {}", requestId);
    }

    @Override
    public Page<FriendRequest> getFriendRequestsByStatus(User user, FriendRequest.Status status, Pageable pageable) {
        UUID id = user.getId();

        log.info("fetching friendRequests of user with user {} and status {}", id, status);

        Page<FriendRequest> friendRequests = friendRequestRepository.findByUserStatus(user, status, pageable);

        log.info("fetched friendRequests of user with user {} and status {}", id, status);

        return friendRequests;
    }
}
