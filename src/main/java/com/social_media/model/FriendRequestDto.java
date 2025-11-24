package com.social_media.model;

public record FriendRequestDto(
        String id,
        String userId,
        String username,
        String friendId,
        String friendName,
        String status
) {
}
