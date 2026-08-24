package com.social_media.service.impl;

import static com.social_media.service.impl.LikeServiceImpl.BLOCKED_USER_MESSAGE;
import static com.social_media.service.impl.PostServiceImpl.POST_NOT_FOUND_MESSAGE;

import com.social_media.entity.*;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.CommentRepository;
import com.social_media.repository.PostRepository;
import com.social_media.repository.UserBlockRepository;
import com.social_media.service.CommentService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
  private static final String COMMENT_NOT_FOUND_MESSAGE = "comment not found";

  private static final String UNABLE_TO_MODIFY_OR_DELETE_COMMENT_MESSAGE =
      "cannot modify or delete the text of another user";

  private final CommentRepository commentRepository;

  private final PostRepository postRepository;

  private final UserBlockRepository userBlockRepository;

  @Override
  @Transactional
  public Comment createComment(Comment comment) {
    UUID id = comment.getUser().getId();

    log.info("creating comment for user with id {}", id);

    Post post =
        postRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

    UUID postAuthorId = post.getUser().getId();

    if (!postAuthorId.equals(id) && userBlockRepository.existsBlockBetween(postAuthorId, id)) {
      throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
    }

    comment.setTime(LocalDateTime.now());

    commentRepository.save(comment);

    log.info("created comment for user with id {}", id);

    return comment;
  }

  @Override
  @Transactional
  public Comment updateComment(User user, UUID id, String text) {
    log.info("updating comment with id {}", id);

    Comment comment =
        commentRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND_MESSAGE));

    if (!comment.getUser().getId().equals(user.getId())) {
      throw new RequestNotAllowedException(UNABLE_TO_MODIFY_OR_DELETE_COMMENT_MESSAGE);
    }

    comment.setText(text);

    commentRepository.save(comment);

    log.info("updated comment with id {}", id);

    return comment;
  }

  @Override
  @Transactional
  public void deleteComment(User user, UUID id) {
    log.info("deleting comment with id {}", id);

    Comment comment =
        commentRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND_MESSAGE));

    if (!comment.getUser().getId().equals(user.getId())) {
      throw new RequestNotAllowedException(UNABLE_TO_MODIFY_OR_DELETE_COMMENT_MESSAGE);
    }

    commentRepository.delete(comment);

    log.info("deleted comment with id {}", id);
  }

  @Override
  public Page<Comment> getCommentsByPostId(UUID postId, UUID userId, Pageable pageable) {
    log.info("fetching comments by postId {}", postId);

    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

    UUID postAuthorId = post.getUser().getId();

    if (!postAuthorId.equals(userId)
        && userBlockRepository.existsBlockBetween(postAuthorId, userId)) {
      throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
    }

    Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

    log.info("fetched comments by postId {}", postId);

    return comments;
  }
}
