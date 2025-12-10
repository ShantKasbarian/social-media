package com.social_media.service;

import com.social_media.entity.Comment;
import com.social_media.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public interface CommentService {
    Comment createComment(User user, Comment comment);
    Comment updateComment(User user, @NotNull(message = "id must be specified") UUID id, String text);
    void deleteComment(User user, @NotNull(message = "id must be specified") UUID id);
    Page<Comment> getCommentsByPostId(@NotNull(message = "id must be specified") UUID postId, Pageable pageable);
}
