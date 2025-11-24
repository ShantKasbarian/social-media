package com.social_media.model;

import java.util.UUID;

public record FriendRequestDto(
        UUID id,
        UUID userId,
        String username,
        UUID friendId,
        String friendName,
        String status
) {
}
