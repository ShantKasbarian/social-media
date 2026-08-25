package com.social_media.model;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record PostDto(
    UUID id,
    UUID userId,
    String username,
    @NotBlank(message = "text must be specified") String text,
    Long likes,
    Instant postedTime) {}
