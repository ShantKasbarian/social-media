package com.social_media.models;

public record CommentDto(
        String id,
        String postId,
        String comment,
        String userId,
        String username,
        String commentedTime
) {
}
