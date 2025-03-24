package com.social_media.models;

public record PostDto(
        String id,
        String userId,
        String username,
        String title,
        Long likes,
        String postedTime
) {
}
