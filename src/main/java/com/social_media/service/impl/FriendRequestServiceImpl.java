package com.social_media.service.impl;

import com.social_media.converter.FriendRequestConverter;
import com.social_media.entity.FriendRequest;
import com.social_media.entity.FriendshipStatus;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.model.FriendRequestDto;
import com.social_media.model.PageDto;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.UserRepository;
import com.social_media.service.FriendRequestService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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

    private static final String UNABLE_TO_DECLINE_FRIEND_REQUEST = "cannot decline friend request";

    private final FriendRequestRepository friendRequestRepository;

    private final UserRepository userRepository;

    private final FriendRequestConverter friendRequestConverter;

    @Override
    @Transactional
    public FriendRequest addFriend(String targetUserId, User user) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

        String currentUserId = user.getId();

        if (friendRequestRepository.isFriendRequestBlockedByUserIdFriendId(currentUserId, targetUserId)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        if (friendRequestRepository.existsByUserIdFriendId(currentUserId, targetUserId)) {
            throw new ResourceAlreadyExistsException(FRIEND_REQUEST_ALREADY_SENT_MESSAGE);
        }

        return friendRequestRepository.save(new FriendRequest(UUID.randomUUID().toString(), user, targetUser, FriendshipStatus.PENDING));
    }

    @Override
    @Transactional
    public FriendRequest acceptFriend(String requestId, User user) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(FRIEND_REQUEST_NOT_FOUND_MESSAGE));

        if (!user.getId().equals(friendRequest.getFriend().getId())) {
            throw new RequestNotAllowedException(FRIEND_REQUEST_NOT_FOUND_MESSAGE);
        }

        if (friendRequest.getStatus().equals(FriendshipStatus.BLOCKED)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        friendRequest.setStatus(FriendshipStatus.ACCEPTED);
        return friendRequestRepository.save(friendRequest);
    }

    @Override
    public PageDto<FriendRequest, FriendRequestDto> getFriends(User user, Pageable pageable) {
        return new PageDto<>(
                friendRequestRepository.findByUserFriend_FriendAndStatus(
                        user,
                        FriendshipStatus.ACCEPTED,
                        pageable
                ), friendRequestConverter
        );
    }

    @Override
    public PageDto<FriendRequest, FriendRequestDto> getPendingUsers(User user, Pageable pageable) {
        return new PageDto<>(
                friendRequestRepository.findByFriend(user, pageable),
                friendRequestConverter
        );
    }

    @Override
    @Transactional
    public FriendRequest declineFriendRequest(String requestId, User user) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(FRIEND_REQUEST_NOT_FOUND_MESSAGE));

        String userId = user.getId();

        if (!friendRequest.getFriend().getId().equals(userId)) {
            throw new RequestNotAllowedException(UNABLE_TO_DECLINE_FRIEND_REQUEST);
        }

        if (friendRequestRepository.isFriendRequestBlocked(requestId)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        friendRequest.setStatus(FriendshipStatus.DECLINED);
        return friendRequestRepository.save(friendRequest);
    }
}
