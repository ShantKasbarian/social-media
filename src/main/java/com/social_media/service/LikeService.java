package com.social_media.service;

import com.social_media.entity.Like;
import com.social_media.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public interface LikeService {
    Like createLike(User user, @NotNull(message = "postId must be specified") UUID id);
    void deleteLikeByPostId(UUID userId, @NotNull(message = "postId must be specified") UUID postId);
}
