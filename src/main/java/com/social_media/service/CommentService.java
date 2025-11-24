package com.social_media.service;

import com.social_media.entity.Comment;
import com.social_media.entity.User;

public interface CommentService {
    Comment comment(String content, String postId, User user);
    Comment editComment(String id, String content, User user);
    void deleteComment(String id, User user);
}
