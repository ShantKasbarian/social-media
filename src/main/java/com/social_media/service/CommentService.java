package com.social_media.service;

import com.social_media.entity.Comment;
import com.social_media.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {
    Comment createComment(User user, Comment comment);
    Comment updateComment(User user, Comment comment);
    void deleteComment(User user, UUID id);
    Page<Comment> getCommentsByPostId(User user, UUID postId, Pageable pageable);
}
