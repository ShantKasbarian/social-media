package com.social_media.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(
        UUID id,
        @NotNull(message = "postId must be specified") UUID postId,
        @NotBlank(message = "text must be specified") String text,
        UUID userId,
        String username,
        LocalDateTime commentedTime
) {
}
