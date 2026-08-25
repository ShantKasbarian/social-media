package com.social_media.service;

import com.social_media.entity.Comment;
import com.social_media.model.CommentDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService
    extends Creatable<Comment, CommentDto>, Updatable<Comment, CommentDto>, Deletable {
  Page<Comment> findByPostId(UUID postId, UUID userId, Pageable pageable);
}
