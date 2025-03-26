package com.social_media.services;

import com.social_media.converters.FriendConverter;
import com.social_media.entities.Friend;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.entities.UserFriend;
import com.social_media.exceptions.RequestNotAllowedException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.exceptions.ResourceNotFoundException;
import com.social_media.models.FriendDto;
import com.social_media.models.PageDto;
import com.social_media.repositories.FriendRepository;
import com.social_media.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;

    private final UserRepository userRepository;

    private final FriendConverter friendConverter;

    public Friend addFriend(String friendId, User user) {
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        UserFriend userFriend = new UserFriend(user, friend);

        if (friendRepository.existsByUserFriend(userFriend)) {
            throw new ResourceAlreadyExistsException("you have already sent a friend request");
        }

        return friendRepository.save(new Friend(UUID.randomUUID().toString(), userFriend, FriendshipStatus.PENDING));
    }

    public Friend acceptFriend(String requestId, User user) {
        Friend friend = friendRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("friend request not found"));

        if (!user.getId().equals(friend.getUserFriend().getFriend().getId())) {
            throw new RequestNotAllowedException("cannot accept friend of another user");
        }

        friend.setStatus(FriendshipStatus.ACCEPTED);
        return friendRepository.save(friend);
    }

    public Friend blockFriend(String requestId, User user) {
        Friend friend = friendRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("friend request not found"));

        if (
                user.getId().equals(friend.getUserFriend().getUser().getId()) ||
                user.getId().equals(friend.getUserFriend().getFriend().getId())
        ) {
            friend.setStatus(FriendshipStatus.BLOCKED);
            return friendRepository.save(friend);
        }

        throw new RequestNotAllowedException("cannot block friend of another user");
    }

    public PageDto<FriendDto> getFriends(User user, Pageable pageable) {
        Page<Friend> friends = friendRepository.findByUserFriend_FriendAndStatus(user, FriendshipStatus.ACCEPTED, pageable);

        PageDto<FriendDto> page = new PageDto<>();

        page.setContent(
                friends.getContent()
                    .stream()
                    .map(friendConverter:: convertToModel)
                    .toList());
        page.setPageNo(pageable.getPageNumber());
        page.setPageSize(friends.getSize());
        page.setTotalElements(friends.getContent().size());
        page.setTotalPages(friends.getTotalPages());
        page.setEmpty(friends.isEmpty());
        return page;
    }

    public PageDto<FriendDto> getBlockedUsers(User user, Pageable pageable) {
        Page<Friend> friends = friendRepository.findByUserFriend_FriendAndStatus(user, FriendshipStatus.BLOCKED, pageable);

        PageDto<FriendDto> page = new PageDto<>();

        page.setContent(
                friends.getContent()
                        .stream()
                        .map(friendConverter:: convertToModel)
                        .toList());
        page.setPageNo(pageable.getPageNumber());
        page.setPageSize(friends.getSize());
        page.setTotalElements(friends.getContent().size());
        page.setTotalPages(friends.getTotalPages());
        page.setEmpty(friends.isEmpty());
        return page;
    }

    public PageDto<FriendDto> getPendingUsers(User user, Pageable pageable) {
        Page<Friend> friends = friendRepository.findByUserFriend_FriendAndStatus(user, FriendshipStatus.PENDING, pageable);

        PageDto<FriendDto> page = new PageDto<>();

        page.setContent(
                friends.getContent()
                        .stream()
                        .map(friendConverter:: convertToModel)
                        .toList());
        page.setPageNo(pageable.getPageNumber());
        page.setPageSize(friends.getSize());
        page.setTotalElements(friends.getContent().size());
        page.setTotalPages(friends.getTotalPages());
        page.setEmpty(friends.isEmpty());

        return page;
    }
}
