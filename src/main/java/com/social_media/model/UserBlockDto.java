package com.social_media.model;

import java.util.UUID;

public record UserBlockDto(
    UUID id, UUID blockerId, String blockerUsername, UUID targetUserId, String targetUsername) {}
