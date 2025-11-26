package com.social_media.service;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public interface FriendRequestService {
    FriendRequest createFriendRequest(User user, @NotNull(message = "id must be specified") UUID targetUserId);
    FriendRequest updateFriendRequestStatus(User user, UUID requestId, FriendRequest.Status status);
    void deleteFriendRequest(User user, @NotNull(message = "id must be specified") UUID requestId);
    Page<FriendRequest> getFriendRequestsByStatus(User user, @NotNull(message = "status must be specified") FriendRequest.Status status, Pageable pageable);
}
