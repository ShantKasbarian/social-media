package com.social_media.models;

public record CommentDto(
        String id,
        String postId,
        String content,
        String commentedTime
) {
}
