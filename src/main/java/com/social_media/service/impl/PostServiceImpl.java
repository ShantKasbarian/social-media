package com.social_media.service.impl;

import com.social_media.annotation.CheckFriendRequestStatus;
import com.social_media.entity.*;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.*;
import com.social_media.service.PostService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Validated
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    public static final String POST_NOT_FOUND_MESSAGE = "post not found";

    private static final String UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE = "unable to delete or modify post";

    private static final String USER_NOT_FOUND_MESSAGE = "user not found";

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public Post createPost(User user, Post post) {
        post.setTime(LocalDateTime.now());
        post.setUser(user);

        return postRepository.save(post);
    }

    @Override
    public Post getPostById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        return post;
    }

    @Override
    @Transactional
    public Post updatePost(User user, UUID id, String title) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException(UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE);
        }

        post.setText(title);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(User user, UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException(UNABLE_TO_DELETE_OR_MODIFY_POST_MESSAGE);
        }

        postRepository.delete(post);
    }

    @Override
    public Page<Post> getFriendsPosts(User user, Pageable pageable) {
        return postRepository.findByUserAcceptedFriendRequests(user.getId(), pageable);
    }

    @Override
    @CheckFriendRequestStatus
    public Page<Post> getUserPosts(User user, UUID userId, Pageable pageable) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE));

        return postRepository.findByUser(targetUser, pageable);
    }

    @Override
    public Page<Post> getUserLikedPosts(User user, Pageable pageable) {
        return postRepository.findByUserLikes(user, pageable);
    }
}
