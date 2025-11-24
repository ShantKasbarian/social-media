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

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;

    private final UserRepository userRepository;

    private final FriendRequestConverter friendRequestConverter;

    @Override
    @Transactional
    public FriendRequest addFriend(String targetUserId, User user) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        List<User> targetUserBlockedUsers = targetUser.getBlockedUsers();
        String currentUserId = user.getId();

        for (User blockedUser: targetUserBlockedUsers) {
            if (blockedUser.getId().equals(currentUserId)) {
                throw new RequestNotAllowedException("user has blocked you");
            }
        }

        List<User> currentUserBlockedUsers = user.getBlockedUsers();

        for (User blockedUser: currentUserBlockedUsers) {
            if (blockedUser.getId().equals(targetUserId)) {
                user.getBlockedUsers().remove(blockedUser);
                userRepository.save(user);
                break;
            }
        }

        if (friendRequestRepository.existsByUser_idFriend_id(currentUserId, targetUserId)) {
            throw new ResourceAlreadyExistsException("you have already sent a friend request");
        }

        return friendRequestRepository.save(new FriendRequest(UUID.randomUUID().toString(), user, targetUser, FriendshipStatus.PENDING));
    }

    @Override
    @Transactional
    public FriendRequest acceptFriend(String requestId, User user) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("friend request not found"));

        if (!user.getId().equals(friendRequest.getFriend().getId())) {
            throw new RequestNotAllowedException("cannot accept friend of another user");
        }

        if (friendRequest.getStatus().equals(FriendshipStatus.BLOCKED)) {
            throw new RequestNotAllowedException("either you or the targeted user has blocked you");
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
                .orElseThrow(() -> new ResourceNotFoundException("friend request not found"));

        if (!friendRequest.getFriend().getId().equals(user.getId())) {
            throw new RequestNotAllowedException("cannot decline friend request");
        }

        String targetUserId = user.getId();
        List<User> blockedUsers = user.getBlockedUsers();

        for(User blockedUser: blockedUsers) {
            if (blockedUser.getId().equals(targetUserId)) {
                throw new RequestNotAllowedException("you have to unblock this user first");
            }
        }

        User targetUser = friendRequest.getUser();
        List<User> targetUserBlockedUsers = targetUser.getBlockedUsers();
        String currentUserId = user.getId();

        for (User blockedUser: targetUserBlockedUsers) {
            if (blockedUser.getId().equals(currentUserId)) {
                throw new RequestNotAllowedException("user has to unblock you first");
            }
        }

        friendRequest.setStatus(FriendshipStatus.DECLINED);
        return friendRequestRepository.save(friendRequest);
    }
}
