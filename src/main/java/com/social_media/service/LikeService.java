package com.social_media.service;

import com.social_media.entity.Like;
import com.social_media.entity.User;

import java.util.UUID;

public interface LikeService {
    Like createLike(User user, UUID id);
    void removeLike(User user, UUID postId);
}
