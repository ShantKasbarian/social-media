package com.social_media.models;

public record FriendDto(
        String id,
        String userId,
        String username,
        String friendId,
        String friendName,
        String status
) {
}
