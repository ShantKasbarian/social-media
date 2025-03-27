package com.social_media.services;

import com.social_media.converters.CommentConverter;
import com.social_media.entities.Comment;
import com.social_media.entities.Post;
import com.social_media.entities.User;
import com.social_media.exceptions.InvalidProvidedInfoException;
import com.social_media.exceptions.RequestNotAllowedException;
import com.social_media.exceptions.ResourceNotFoundException;
import com.social_media.repositories.CommentRepository;
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

    private final CommentConverter commentConverter;

    private final PostRepository postRepository;

    @Transactional
    public Comment comment(String content, String postId, User user) {
        if (content == null || content.isEmpty()) {
            throw new InvalidProvidedInfoException("comment is empty");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

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
    public Comment editComment(String id, String content,User user) {
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
