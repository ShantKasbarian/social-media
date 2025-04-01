package com.social_media.models;

public record FriendRequestDto(
        String id,
        String userId,
        String username,
        String friendId,
        String friendName,
        String status,
        String blockerId
) {
}
