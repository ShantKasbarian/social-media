package com.social_media.services;

import com.social_media.entities.*;
import com.social_media.exceptions.InvalidProvidedInfoException;
import com.social_media.exceptions.RequestNotAllowedException;
import com.social_media.exceptions.ResourceNotFoundException;
import com.social_media.repositories.CommentRepository;
import com.social_media.repositories.FriendRequestRepository;
import com.social_media.repositories.PostRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final FriendRequestRepository friendRequestRepository;

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
