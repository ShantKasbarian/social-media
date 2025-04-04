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
    public FriendRequest addFriend(String friendId, User user) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        if (friendRequestRepository.existsByUser_idFriend_id(user.getId(), friendId)) {
            throw new ResourceAlreadyExistsException("you have already sent a friend request");
        }

        return friendRequestRepository.save(new FriendRequest(UUID.randomUUID().toString(), user, friend, FriendshipStatus.PENDING));
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

    @Transactional
    public FriendRequest blockFriend(String requestId, User user) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("friend request not found"));

        if (
                !user.getId().equals(friendRequest.getUser().getId()) &&
                !user.getId().equals(friendRequest.getFriend().getId())
        ) {
            throw new RequestNotAllowedException("cannot block friend of another user");
        }

        User target = null;

        if (friendRequest.getFriend().getId().equals(user.getId())) {
            target = friendRequest.getUser();
        }
        else {
            target = friendRequest.getFriend();
        }

        user.getBlockedUsers().add(target);
        userRepository.save(user);
        friendRequest.setStatus(FriendshipStatus.BLOCKED);
        return friendRequestRepository.save(friendRequest);
    }

    @Transactional
    public FriendRequest unblockFriend(String id, User user) {
        FriendRequest friendRequest = friendRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("friend request not found"));

        User target = null;

        if (friendRequest.getFriend().getId().equals(user.getId())) {
            target = friendRequest.getUser();
        }
        else {
            target = friendRequest.getFriend();
        }

        List<User> blockedUsers = user.getBlockedUsers();

        if (blockedUsers.isEmpty()) {
            throw new ResourceNotFoundException("you haven't blocked this user");
        }

        for (int i = 0; i < blockedUsers.size(); i++) {
            User blockedUser = blockedUsers.get(i);

            if (blockedUser.getId().equals(target.getId())) {
                user.getBlockedUsers().remove(blockedUser);
                break;
            }

            if (i == blockedUsers.size() - 1) {
                throw new ResourceNotFoundException("you haven't blocked this user");
            }
        }

        userRepository.save(user);
        friendRequest.setStatus(FriendshipStatus.PENDING);

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

    public PageDto<FriendRequest, FriendRequestDto> getBlockedUsers(User user, Pageable pageable) {
        return new PageDto<>(friendRequestRepository.findByUserFriend_FriendAndStatus(user, FriendshipStatus.BLOCKED, pageable), friendRequestConverter);
    }

    public PageDto<FriendRequest, FriendRequestDto> getPendingUsers(User user, Pageable pageable) {
        return new PageDto<>(
                friendRequestRepository.findByUserFriend_FriendAndStatus(user, FriendshipStatus.PENDING, pageable),
                friendRequestConverter
        );
    }
}
