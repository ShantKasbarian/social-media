package com.social_media.service;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FriendRequestService {
    FriendRequest createFriendRequest(User user, UUID targetUserId);
    FriendRequest updateFriendRequestStatus(User user, UUID requestId, FriendRequest.Status status);
    void deleteFriendRequest(User user, UUID requestId);
    Page<FriendRequest> getFriendRequestsByStatus(User user, FriendRequest.Status status,Pageable pageable);
}
