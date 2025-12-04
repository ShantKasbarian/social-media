package com.social_media.service.impl;

import com.social_media.annotation.ValidateUserNotBlocked;
import com.social_media.entity.*;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.CommentRepository;
import com.social_media.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final String COMMENT_NOT_FOUND_MESSAGE = "text not found";

    private static final String UNABLE_TO_MODIFY_OR_DELETE_COMMENT_MESSAGE = "cannot modify or delete the text of another user";

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    @ValidateUserNotBlocked
    public Comment createComment(User user, Comment comment) {
        UUID id = user.getId();

        log.info("creating comment for user with id {}", id);

        comment.setTime(LocalDateTime.now());
        comment.setUser(user);

        commentRepository.save(comment);

        log.info("created comment for user with id {}", id);

        return comment;
    }

    @Override
    @Transactional
    public Comment updateComment(User user, UUID id, String text) {
        log.info("updating comment with id {}", id);

        Comment comment = commentRepository.findById(id)
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

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND_MESSAGE));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException(UNABLE_TO_MODIFY_OR_DELETE_COMMENT_MESSAGE);
        }

        commentRepository.delete(comment);

        log.info("deleted comment with id {}", id);
    }

    @Override
    @ValidateUserNotBlocked
    public Page<Comment> getCommentsByPostId(User user, UUID postId, Pageable pageable) {
        log.info("fetching comments by postId {}", postId);

        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

        log.info("fetched comments by postId {}", postId);

        return comments;
    }
}
