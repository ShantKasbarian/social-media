package com.social_media.model;

public record LikeDto(
        String id,
        String userId,
        String username,
        String postId
) {
}
