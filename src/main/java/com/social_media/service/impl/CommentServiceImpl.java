package com.social_media.service.impl;

import com.social_media.entity.*;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.CommentRepository;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.PostRepository;
import com.social_media.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final String POST_NOT_FOUND_MESSAGE = "post not found";

    private static final String BLOCKED_MESSAGE = "you have been blocked by this user";

    private static final String COMMENT_NOT_FOUND_MESSAGE = "text not found";

    private static final String UNABLE_TO_MODIFY_OR_DELETE_COMMENT_MESSAGE = "cannot modify or delete the text of another user";

    private static final String BLOCKED_USER_MESSAGE = "you have blocked or have been blocked by this user";

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final FriendRequestRepository friendRequestRepository;

    @Override
    @Transactional
    public Comment createComment(User user, Comment comment) {
        FriendRequest friendRequest = friendRequestRepository.findByUserIdTargetUserId(
                user.getId(), comment.getPost().getUser().getId()).orElse(null);

        if (friendRequest != null && friendRequest.getStatus().equals(FriendRequest.Status.BLOCKED)) {
            throw new RequestNotAllowedException(BLOCKED_MESSAGE);
        }

        comment.setTime(LocalDateTime.now());
        comment.setUser(user);

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(User user, UUID id, String text) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND_MESSAGE));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException(UNABLE_TO_MODIFY_OR_DELETE_COMMENT_MESSAGE);
        }

        comment.setText(text);

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(User user, UUID id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND_MESSAGE));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException(UNABLE_TO_MODIFY_OR_DELETE_COMMENT_MESSAGE);
        }

        commentRepository.delete(comment);
    }

    @Override
    public Page<Comment> getCommentsByPostId(User user, UUID postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (friendRequestRepository.existsByUserIdTargetUserIdStatus(user.getId(), post.getUser().getId(), FriendRequest.Status.BLOCKED)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        return commentRepository.findByPostId(postId, pageable);
    }
}
