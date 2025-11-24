package com.social_media.model;

public record PostDto(
        String id,
        String userId,
        String username,
        String title,
        Long likes,
        String postedTime
) {
}
