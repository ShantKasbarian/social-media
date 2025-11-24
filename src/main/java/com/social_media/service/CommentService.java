package com.social_media.service;

import com.social_media.entity.Comment;
import com.social_media.entity.User;

import java.util.UUID;

public interface CommentService {
    Comment comment(User user, UUID postId, String content);
    Comment editComment(User user, UUID id, String content);
    void deleteComment(User user, UUID id);
}
