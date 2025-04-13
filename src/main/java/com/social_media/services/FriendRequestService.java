package com.social_media.services;

import com.social_media.converters.FriendRequestConverter;
import com.social_media.entities.FriendRequest;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.exceptions.RequestNotAllowedException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.exceptions.ResourceNotFoundException;
import com.social_media.models.FriendRequestDto;
import com.social_media.models.PageDto;
import com.social_media.repositories.FriendRequestRepository;
import com.social_media.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;

    private final UserRepository userRepository;

    private final FriendRequestConverter friendRequestConverter;

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

    public PageDto<FriendRequest, FriendRequestDto> getFriends(User user, Pageable pageable) {
        return new PageDto<>(
                friendRequestRepository.findByUserFriend_FriendAndStatus(
                        user,
                        FriendshipStatus.ACCEPTED,
                        pageable
                ), friendRequestConverter
        );
    }

    public PageDto<FriendRequest, FriendRequestDto> getPendingUsers(User user, Pageable pageable) {
        return new PageDto<>(
                friendRequestRepository.findByUserFriend_FriendAndStatus(user, FriendshipStatus.PENDING, pageable),
                friendRequestConverter
        );
    }
}
