package com.social_media.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PatchCommentDto(
    @NotNull(message = "id must be specified") UUID id,
    @NotBlank(message = "text must be specified") String text) {}
