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
    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final FriendRequestRepository friendRequestRepository;

    @Override
    @Transactional
    public Comment comment(String content, String postId, User user) {
        if (content == null || content.isEmpty()) {
            throw new InvalidProvidedInfoException("comment is empty");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        FriendRequest friendRequest = friendRequestRepository.findByUser_idFriend_id(user.getId(), post.getUser().getId())
                .orElse(null);

        if (friendRequest != null && friendRequest.getStatus().equals(FriendshipStatus.BLOCKED)) {
            throw new RequestNotAllowedException("you have been blocked by this user");
        }

        return commentRepository.save(
                new Comment(
                        UUID.randomUUID().toString(),
                        content,
                        LocalDateTime.now(),
                        post,
                        user
                )
        );
    }

    @Override
    @Transactional
    public Comment editComment(String id, String content, User user) {
        if (content == null || content.isEmpty()) {
            throw new InvalidProvidedInfoException("content must be specified");
        }

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException("cannot modify the comment of another user");
        }

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(String id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException("cannot delete the comment of another user");
        }

        commentRepository.delete(comment);
    }
}
