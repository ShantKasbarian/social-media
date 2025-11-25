package com.social_media.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(
        UUID id,
        UUID postId,
        String text,
        UUID userId,
        String username,
        LocalDateTime commentedTime
) {
}
