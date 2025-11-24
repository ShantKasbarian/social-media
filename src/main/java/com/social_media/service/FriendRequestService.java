package com.social_media.service;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FriendRequestService {
    FriendRequest addFriend(User user, UUID targetUserId);
    FriendRequest acceptFriend(User user, UUID requestId);
    Page<FriendRequest> getFriends(User user, Pageable pageable);
    Page<FriendRequest> getPendingUsers(User user, Pageable pageable);
    FriendRequest declineFriendRequest(User user, UUID requestId);
}
