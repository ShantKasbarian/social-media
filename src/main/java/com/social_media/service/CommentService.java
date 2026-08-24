package com.social_media.service;

import com.social_media.entity.Comment;
import com.social_media.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
  Comment createComment(Comment comment);

  Comment updateComment(User user, UUID id, String text);

  void deleteComment(User user, UUID id);

  Page<Comment> getCommentsByPostId(UUID postId, UUID userId, Pageable pageable);
}
