package com.social_media.service.impl;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.UserRepository;
import com.social_media.service.FriendRequestService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
    private static final String USER_NOT_FOUND_MESSAGE = "user not found";

    private static final String BLOCKED_USER_MESSAGE = "user has blocked you";

    private static final String FRIEND_REQUEST_ALREADY_SENT_MESSAGE = "you have already sent a friend request";

    private static final String FRIEND_REQUEST_NOT_FOUND_MESSAGE = "friend request not found";

    private static final String UNABLE_TO_DELETE_FRIEND_REQUEST = "cannot delete friend request";

    private final FriendRequestRepository friendRequestRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public FriendRequest createFriendRequest(User user, UUID targetUserId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

        UUID currentUserId = user.getId();

        if (friendRequestRepository.existsByUserIdTargetUserIdStatus(currentUserId, targetUserId, FriendRequest.Status.BLOCKED)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        if (friendRequestRepository.existsByUserIdTargetUserId(currentUserId, targetUserId)) {
            throw new ResourceAlreadyExistsException(FRIEND_REQUEST_ALREADY_SENT_MESSAGE);
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUser(user);
        friendRequest.setTargetUser(targetUser);
        friendRequest.setStatus(FriendRequest.Status.PENDING);

        return friendRequestRepository.save(friendRequest);
    }

    @Override
    @Transactional
    public FriendRequest updateFriendRequestStatus(User user, UUID requestId, FriendRequest.Status status) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(FRIEND_REQUEST_NOT_FOUND_MESSAGE));

        if (!user.getId().equals(friendRequest.getTargetUser().getId())) {
            throw new RequestNotAllowedException(FRIEND_REQUEST_NOT_FOUND_MESSAGE);
        }

        if (friendRequest.getStatus().equals(FriendRequest.Status.BLOCKED)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        friendRequest.setStatus(status);

        return friendRequestRepository.save(friendRequest);
    }

    @Override
    @Transactional
    public void deleteFriendRequest(User user, UUID requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(FRIEND_REQUEST_NOT_FOUND_MESSAGE));

        UUID userId = user.getId();

        if (
                !friendRequest.getTargetUser().getId().equals(userId) &&
                friendRequest.getStatus().equals(FriendRequest.Status.BLOCKED)
        ) {
            throw new RequestNotAllowedException(UNABLE_TO_DELETE_FRIEND_REQUEST);
        }

        friendRequestRepository.delete(friendRequest);
    }

    @Override
    public Page<FriendRequest> getFriendRequestsByStatus(User user, FriendRequest.Status status, Pageable pageable) {
        return friendRequestRepository.findByUserStatus(user, status, pageable);
    }
}
