package com.social_media.model;

import com.social_media.entity.FriendRequest;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FriendRequestDto(
        @NotNull(message = "id must be specified") UUID id,
        UUID userId,
        String username,
        UUID targetUserId,
        String targetUsername,
        @NotNull(message = "status must be specified") FriendRequest.Status status
) {
}
