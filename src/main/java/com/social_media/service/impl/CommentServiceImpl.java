package com.social_media.service.impl;

import com.social_media.entity.*;
import com.social_media.exception.InvalidProvidedInfoException;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.CommentRepository;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.PostRepository;
import com.social_media.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final String POST_NOT_FOUND_MESSAGE = "post not found";

    private static final String BLOCKED_MESSAGE = "you have been blocked by this user";

    private static final String COMMENT_NOT_FOUND_MESSAGE = "comment not found";

    private static final String UNABLE_TO_MODIFY_OR_DELETE_COMMENT_MESSAGE = "cannot modify or delete the comment of another user";

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final FriendRequestRepository friendRequestRepository;

    @Override
    @Transactional
    public Comment comment(User user, UUID postId, String content) {
        if (content == null || content.isEmpty()) {
            throw new InvalidProvidedInfoException("comment is empty");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        FriendRequest friendRequest = friendRequestRepository.findByUserIdFriendId(user.getId(), post.getUser().getId())
                .orElse(null);

        if (friendRequest != null && friendRequest.getStatus().equals(FriendRequest.Status.BLOCKED)) {
            throw new RequestNotAllowedException(BLOCKED_MESSAGE);
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCommentedTime(LocalDateTime.now());
        comment.setPost(post);
        comment.setUser(user);

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment editComment(User user, UUID id, String content) {
        if (content == null || content.isEmpty()) {
            throw new InvalidProvidedInfoException("content must be specified");
        }

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMMENT_NOT_FOUND_MESSAGE));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException(UNABLE_TO_MODIFY_OR_DELETE_COMMENT_MESSAGE);
        }

        comment.setContent(content);
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
}
