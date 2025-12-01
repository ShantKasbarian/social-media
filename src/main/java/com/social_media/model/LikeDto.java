package com.social_media.model;

import java.util.UUID;

public record LikeDto(
        UUID id,
        UUID userId,
        String username,
        UUID postId
) {
}
