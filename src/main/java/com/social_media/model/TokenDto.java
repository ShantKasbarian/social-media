package com.social_media.model;

import java.util.UUID;

public record TokenDto(
        String token,
        String username,
        UUID userId
) {
}
