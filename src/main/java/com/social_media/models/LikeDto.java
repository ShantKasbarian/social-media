package com.social_media.models;

public record LikeDto(
        String id,
        String userId,
        String username,
        String postId
) {
}
