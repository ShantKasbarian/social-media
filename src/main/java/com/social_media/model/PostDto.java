package com.social_media.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostDto(
        UUID id,
        UUID userId,
        String username,
        String title,
        Long likes,
        LocalDateTime postedTime
) {
}
