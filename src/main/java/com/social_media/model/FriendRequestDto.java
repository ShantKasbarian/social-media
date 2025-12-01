package com.social_media.model;

import com.social_media.entity.FriendRequest;

import java.util.UUID;

public record FriendRequestDto(
        UUID id,
        UUID userId,
        String username,
        UUID targetUserId,
        String targetUsername,
        FriendRequest.Status status
) {
}
