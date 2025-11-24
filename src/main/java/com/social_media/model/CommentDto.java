package com.social_media.model;

public record CommentDto(
        String id,
        String postId,
        String comment,
        String userId,
        String username,
        String commentedTime
) {
}
