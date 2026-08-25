package com.social_media.service;

import com.social_media.entity.Comment;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
  Comment createComment(User user, CommentDto commentDto);

  Comment updateComment(User user, CommentDto commentDto);

  void deleteComment(User user, UUID id);

  Page<Comment> getCommentsByPostId(UUID postId, UUID userId, Pageable pageable);
}
