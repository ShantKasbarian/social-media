package com.social_media.model;

import java.util.UUID;

public record CommentDto(
        UUID id,
        UUID postId,
        String comment,
        UUID userId,
        String username,
        String commentedTime
) {
}
