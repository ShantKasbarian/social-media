package com.social_media.service.impl;

import com.social_media.entity.FriendRequest;
import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.exception.RequestNotAllowedException;
import com.social_media.exception.ResourceAlreadyExistsException;
import com.social_media.exception.ResourceNotFoundException;
import com.social_media.repository.FriendRequestRepository;
import com.social_media.repository.LikeRepository;
import com.social_media.repository.PostRepository;
import com.social_media.service.LikeService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class LikeServiceImpl implements LikeService {
    private static final String POST_NOT_FOUND_MESSAGE = "post not found";

    private static final String BLOCKED_USER_MESSAGE = "you have blocked or have been blocked by this user";

    private static final String TOO_MANY_LIKES_MESSAGE = "cannot like post more than once";

    private static final String LIKE_NOT_FOUND_MESSAGE = "liked not found";

    private final LikeRepository likeRepository;

    private final PostRepository postRepository;

    private final FriendRequestRepository friendRequestRepository;

    @Override
    @Transactional
    public Like createLike(User user, UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        FriendRequest friendRequest = friendRequestRepository
                .findByUserIdTargetUserId(user.getId(), post.getUser().getId())
                .orElse(null);

        if (friendRequest != null && friendRequest.getStatus().equals(FriendRequest.Status.BLOCKED)) {
            throw new RequestNotAllowedException(BLOCKED_USER_MESSAGE);
        }

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new ResourceAlreadyExistsException(TOO_MANY_LIKES_MESSAGE);
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        return likeRepository.save(like);
    }

    @Override
    @Transactional
    public void removeLike(User user, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE));

        Like like = likeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new ResourceNotFoundException(LIKE_NOT_FOUND_MESSAGE));

        likeRepository.delete(like);
    }
}
